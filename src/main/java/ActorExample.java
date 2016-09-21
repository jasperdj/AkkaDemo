import akka.actor.*;
import akka.pattern.Patterns;
import akka.util.Timeout;

import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.Future;

/**
 * Created by jasper.dejong on 20-9-2016.
 */
public class ActorExample {

    static Timeout timeout = new Timeout(Duration.create(5, "seconds"));

    public static void main(String[] args) throws Exception {
        ActorSystem system = ActorSystem.create("system");
        ActorRef piet = system.actorOf(Props.create(Piet.class), "piet");

        piet.tell(Piet.HELLO, null);

        Future<Object> name = Patterns.ask(piet,Piet.GET_NAME, timeout);
        Object response = Await.result(name, timeout.duration());
        System.out.println(response);
    }

}

class Piet extends UntypedActor {
    public final static String HELLO = "hello";
    public final static String GET_NAME = "What is your name?";

    public void onReceive(Object message) {
        if (message.equals(HELLO)) {
            System.out.println("Hi!");
        } else if (message.equals(GET_NAME)) {
            getSender().tell("I'm piet", self());
        }
    }
}

