package actors;

import javax.inject.Inject;

import play.Logger;
import play.libs.akka.InjectedActorSupport;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class UserParentActor extends UntypedActor implements InjectedActorSupport {

    public static class Create {
        private final String id;
        private final ActorRef out;

        public Create(final String id, final ActorRef out) {
            this.id = id;
            this.out = out;
        }
    }

    private final UserActor.Factory childFactory;

    @Inject
    public UserParentActor(final UserActor.Factory childFactory) {
        this.childFactory = childFactory;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof UserParentActor.Create) {
            final UserParentActor.Create create = (UserParentActor.Create) message;
            Logger.info("Pusts: " + create.out.toString());
            final ActorRef child = injectedChild(() -> childFactory.create(create.out), "userActor-" + create.id);
            sender().tell(child, self());
        }
    }
}