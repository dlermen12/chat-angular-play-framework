import play.libs.akka.AkkaGuiceSupport;
import actors.UserActor;
import actors.UserParentActor;

import com.google.inject.AbstractModule;

public class Module extends AbstractModule implements AkkaGuiceSupport {
    @Override
    protected void configure() {
        bindActor(UserParentActor.class, "userParentActor");
        bindActorFactory(UserActor.class, UserActor.Factory.class);
    }
}