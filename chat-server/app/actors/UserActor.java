package actors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.assistedinject.Assisted;

public class UserActor extends UntypedActor {

    private static final List<UserActor> actors = new ArrayList<UserActor>();
    private final ActorRef out;
    private UserInfo userInfo;
    private final ObjectMapper mapper;

    @Inject
    public UserActor(@Assisted final ActorRef out) {
        this.out = out;
        mapper = new ObjectMapper();
    }

    @Override
    public void preStart() throws Exception {
        actors.add(this);
        super.preStart();
    }

    @Override
    public void onReceive(final Object msg) throws Exception {
        final ObjectNode node = (ObjectNode) msg;
        final ObjectNode response;
        final TextNode userNode = (TextNode) node.get("userName");
        if (userNode != null) {
            final UserInfo userInfo = mapper.readValue(msg.toString(), UserInfo.class);
            this.userInfo = userInfo;
            response = updateUsers();
            final ObjectNode newUser = mapper.createObjectNode();
            newUser.set("message", new TextNode(userInfo.getUserName() + " entrou!"));
            actors.forEach(x -> x.out.tell(newUser, x.getSelf()));

        } else {
            node.set("message", new TextNode(userInfo.getUserName() + ": " + node.get("message").asText()));
            response = node;
        }
        actors.forEach(x -> x.out.tell(response, x.getSelf()));
    }

    public ObjectNode updateUsers() {
        final ObjectNode node = mapper.createObjectNode();
        final ArrayNode arrays = mapper.createArrayNode();
        node.set("users", arrays);
        actors.stream().forEach(x -> arrays.addPOJO(x.getUserInfo()));

        return node;
    }

    @Override
    public void postStop() throws Exception {
        actors.remove(this);
        final ObjectNode node = mapper.createObjectNode();
        node.set("message", new TextNode(userInfo.getUserName() + " saiu!"));
        actors.forEach(x -> x.out.tell(node, x.getSelf()));
        actors.forEach(x -> x.out.tell(updateUsers(), x.getSelf()));

        super.postStop();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public interface Factory {
        Actor create(ActorRef out);
    }
}
