package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import org.reactivestreams.Publisher;

import play.libs.F.Either;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import scala.compat.java8.FutureConverters;
import actors.UserParentActor;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Status;
import akka.japi.Pair;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import com.fasterxml.jackson.databind.JsonNode;

public class WebSocketController extends Controller {

    private final ActorRef userParentActor;
    private final Materializer materializer;
    private final ActorSystem actorSystem;

    @Inject
    public WebSocketController(final ActorSystem actorSystem, final Materializer materializer, @Named("userParentActor") final ActorRef userParentActor) {
        this.userParentActor = userParentActor;
        this.materializer = materializer;
        this.actorSystem = actorSystem;
    }

    public WebSocket ws() {
        return WebSocket.Json.acceptOrResult(request -> {
            final CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> future = wsFutureFlow(request);
            final CompletionStage<Either<Result, Flow<JsonNode, JsonNode, ?>>> stage = future.thenApplyAsync(Either::Right);
            return stage.exceptionally(this::logException);
        });
    }

    public CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> wsFutureFlow(final Http.RequestHeader request) {
        final Pair<ActorRef, Publisher<JsonNode>> pair = createWebSocketConnections();
        final ActorRef webSocketOut = pair.first();
        final Publisher<JsonNode> webSocketIn = pair.second();
        final String id = String.valueOf(request._underlyingHeader().id());
        final CompletionStage<ActorRef> userActorFuture = createUserActor(id, webSocketOut);
        final CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> stage = userActorFuture.thenApplyAsync(userActor -> createWebSocketFlow(webSocketIn, userActor));
        return stage;
    }

    public CompletionStage<ActorRef> createUserActor(final String id, final ActorRef webSocketOut) {
        final long timeoutMillis = 100L;
        return FutureConverters.toJava(ask(userParentActor, new UserParentActor.Create(id, webSocketOut), timeoutMillis)).thenApply(stageObj -> (ActorRef) stageObj);
    }

    public Pair<ActorRef, Publisher<JsonNode>> createWebSocketConnections() {
        final Source<JsonNode, ActorRef> source = Source.actorRef(10, OverflowStrategy.dropTail());
        final Sink<JsonNode, Publisher<JsonNode>> sink = Sink.asPublisher(AsPublisher.WITHOUT_FANOUT);
        final Pair<ActorRef, Publisher<JsonNode>> pair = source.toMat(sink, Keep.both()).run(materializer);
        return pair;
    }

    public Either<Result, Flow<JsonNode, JsonNode, ?>> logException(final Throwable throwable) {
        final Result result = Results.internalServerError("error");
        return Either.Left(result);
    }

    public Flow<JsonNode, JsonNode, NotUsed> createWebSocketFlow(final Publisher<JsonNode> webSocketIn, final ActorRef userActor) {
        final Sink<JsonNode, NotUsed> sink = Sink.actorRef(userActor, new Status.Success("success"));
        final Source<JsonNode, NotUsed> source = Source.fromPublisher(webSocketIn);
        final Flow<JsonNode, JsonNode, NotUsed> flow = Flow.fromSinkAndSource(sink, source);

        return flow.watchTermination((ignore, termination) -> {
            termination.whenComplete((done, throwable) -> {
                actorSystem.stop(userActor);
            });
            return NotUsed.getInstance();
        });
    }

}