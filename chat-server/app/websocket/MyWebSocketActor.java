package websocket;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MyWebSocketActor extends UntypedActor {

    public static Props props(final ActorRef out) {
        return Props.create(MyWebSocketActor.class, out);
    }

    private final ActorRef out;

    public MyWebSocketActor(final ActorRef out) {
        this.out = out;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof String) {
            out.tell("I received your message: " + message, self());
        }
    }
}