/**
 * Created by jasper.dejong on 19-9-2016.
 */


import akka.actor.*;
import akka.pattern.Patterns;
import akka.util.Timeout;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import scala.concurrent.Future;

public class animalSimulator {
    static Timeout timeout = new Timeout(Duration.create(5, "seconds"));

    public static void main(String[] args) throws Exception {
        final ActorSystem actorSystem = ActorSystem.create("world", ConfigFactory.load().getConfig("system1"));
         ActorRef cow = actorSystem.actorOf(Props.create(Animal.class), "cow");
         ActorRef chicken = actorSystem.actorOf(Props.create(Animal.class), "chicken");

        //ActorSelection cow = actorSystem.actorSelection("akka.tcp://world@127.0.0.1:5151/user/cow");

        JFrame f = getWindow();

        cow.tell(new Properties(200, 200, 50, Color.red), null);
        chicken.tell(new Properties(100, 100, 25, Color.blue), null);

        while (true) {
            ArrayList<Body> bodies = new ArrayList<Body>();

            Future<Object> futureCow = Patterns.ask(cow, "where are you?", timeout);
            bodies.add( (Body) Await.result(futureCow, timeout.duration()));

            Future<Object> futureChicken = Patterns.ask(chicken, "where are you?", timeout);
            bodies.add( (Body) Await.result(futureChicken, timeout.duration()));

            f.add(new BodyPainter(bodies));
            f.revalidate();

            Thread.sleep(1);
        }
    }

    static JFrame getWindow() {
        JFrame f = new JFrame();
        f.setSize(500, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        return f;
    }
}

class createRemoteActors {
    public static void main(String[] args) {
        final ActorSystem actorSystem = ActorSystem.create("world");
        final ActorRef cow = actorSystem.actorOf(Props.create(Animal.class), "cow");
        final ActorRef chicken = actorSystem.actorOf(Props.create(Animal.class), "chicken");
    }
}

class Properties {
    public int x;
    public int y;
    public int size;
    public Color color;

    public Properties(int x, int y, int size, Color color) {
        this.x = x;
        this.y=y;
        this.size = size;
        this.color = color;
    }
}

class Animal extends UntypedActor {
    Random random = new Random();
    private int x;
    private int y;
    private int size;
    private Color color;

    public void onReceive(Object message) {
        if (message instanceof Properties) {
            Properties properties = (Properties)message;
            x = properties.x;
            y = properties.y;
            size = properties.size;
            color = properties.color;
        } else if (message.equals("where are you?")) {
            x = Body.randomLocation(x);
            y = Body.randomLocation(y);
            getSender().tell(new Body(x, y, size, size, color), self());
        }
    }
}

class BodyPainter extends JPanel {
    ArrayList<Body> bodies;

    public BodyPainter(ArrayList<Body> bodies) {
        this.bodies = bodies;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Body body : bodies) {
            g2.setColor(body.color);
            g2.fill(body.rect);
        }
    }
}

class Body extends JPanel {
    private static final int PREF_W = 500;
    private static final int PREF_H = PREF_W;
    public Rectangle rect;
    public Color color;
    static Random random = new Random();

    static int randomLocation(int l) {
        if (l < 0) {
            return l + random.nextInt(2);
        } else if (l > 400) {
            return l-random.nextInt(2);
        }
        return l + (random.nextInt(5)-2);
    }

    public Body(int x, int y, int width, int height, Color color) {
        rect = new Rectangle(x, y, width, height);
        this.color = color;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.fill(rect);
    }
}