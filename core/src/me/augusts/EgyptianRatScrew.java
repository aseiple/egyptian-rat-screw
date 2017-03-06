package me.augusts;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.Collections;

public class EgyptianRatScrew extends ApplicationAdapter {
    //Card & screen size constants
    final static float CARD_WIDTH = 1f;
    final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;
    private final static float MINIMUM_VIEWPORT_SIZE = 5f;
    //Player declaration
    static Player player1;
    static Player player2;
    static Status player1Status;
    static Status player2Status;
    //Control variables
    static boolean gameover = false;
    private static Deck deck;
    private static CardBatch cards;
    //General scene declarations
    private OrthographicCamera camera;
    private Model tableTopModel;
    private ModelInstance tableTop;
    private Environment environment;
    //Card declarations
    private ModelBatch modelBatch;
    private TextureAtlas textureAtlas;
    //UI elements declaration
    private Skin skin;
    private Stage stage;
    private Label p1Label;
    private Label p2Label;

    static void dealCards() {
        //Clears current decks
        player1.playerDeck.clear();
        player2.playerDeck.clear();
        CenterDeck.centerCards.clear();
        //Loops through all cards in game
        for (Card card : deck.cards) {
            //Deal until one player has half the deck
            if (player1.playerDeck.size() < deck.getDeckSize() / 2) {
                player1.playerDeck.add(card);
                //Place face down in player slot
                card.position.set(2, -1.5f, player1.playerDeck.size() + 1);
                card.angle = 180;
                card.update();
                cards.add(card);
            } else {
                player2.playerDeck.add(card);
                //Place face down in player slot
                card.position.set(-2, 1.5f, player2.playerDeck.size() + 1);
                card.angle = 180;
                card.update();
                cards.add(card);
            }
        }
    }

    @Override
    public void create() {
        //UI stage
        stage = new Stage();
        //UI style elements
        skin = new Skin();
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
        //Create table for UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        //Create labels
        p1Label = new Label("21 Cards left", skin);
        p2Label = new Label("21 Cards left", skin);
        //Add labels to table
        table.add(p1Label).padBottom(300).padRight(150);
        table.row();
        table.add(p2Label).padLeft(150);
        //Establish input tracker
        InputHandler inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);
        //Create card textures
        modelBatch = new ModelBatch();
        textureAtlas = new TextureAtlas("core/assets/carddeck.atlas");
        Material material = new Material(
                TextureAttribute.createDiffuse(textureAtlas.getTextures().first()),
                new BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f));
        cards = new CardBatch(material);
        //Set deck textures
        deck = new Deck(textureAtlas, 3);
        //Shuffle deck
        deck.shuffle();
        //Create players
        player1 = new Player();
        player1Status = Status.Deal;
        player2 = new Player();
        player2Status = Status.Waiting;
        //Deal cards to all players
        dealCards();
        //Set up camera
        camera = new OrthographicCamera();
        camera.position.set(0, 0, 60);
        camera.lookAt(0, 0, 0);
        //Create table
        ModelBuilder builder = new ModelBuilder();
        builder.begin();
        builder.node().id = "top";
        //noinspection deprecation
        builder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(new Color(0x63750A))))
                .box(0f, 0f, -0.5f, 20f, 20f, 1f);
        tableTopModel = builder.end();
        tableTop = new ModelInstance(tableTopModel);
        //Create lighting for table
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f));
    }

    @Override
    public void render() {
        //Clear existing frame
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        //Begin drawing frame
        modelBatch.begin(camera);
        modelBatch.render(tableTop, environment);
        //Check if any player is out of cards
        if (player1.playerDeck.size() == 0) {
            //End game
            modelBatch.end();
            player2Wins().draw();
            gameover = true;
        } else if (player2.playerDeck.size() == 0) {
            //End game
            modelBatch.end();
            player1Wins().draw();
            gameover = true;
        } else {
            //Render cards
            modelBatch.render(cards, environment);
            modelBatch.end();
            //Provide instructional messages
            if (player1Status == Status.Deal) {
                p1Label.setText("It's your turn\n" + player1.playerDeck.size() + " Cards left\n" + "To deal card press - A\n" + "To slap press - S");
                p2Label.setText("To deal card press - K\n" + player2.playerDeck.size() + " Cards left\n" + "To slap press - L");
            } else {
                p1Label.setText("To deal card press - A\n" + player1.playerDeck.size() + " Cards left\n" + "To slap press - S");
                p2Label.setText("It's your turn\n" + player2.playerDeck.size() + " Cards left\n" + "To deal card press - K\n" + "To slap press - L");
            }
            //Draw UI
            stage.draw();
        }
    }

    private Stage player1Wins() {
        Stage p1Stage = new Stage();
        Label.LabelStyle style = new Label.LabelStyle();
        style.fontColor = Color.WHITE;
        style.font = new BitmapFont();
        Label win = new Label("Player 1 Wins", style);
        Label restart = new Label("Press Space to restart", style);
        Table winTable = new Table();
        winTable.setFillParent(true);
        p1Stage.addActor(winTable);
        winTable.add(win);
        winTable.row();
        winTable.add(restart);
        return p1Stage;
    }

    private Stage player2Wins() {
        Stage p2Stage = new Stage();
        Label.LabelStyle style = new Label.LabelStyle();
        style.fontColor = Color.WHITE;
        style.font = new BitmapFont();
        Label win = new Label("Player 2 Wins", style);
        Label restart = new Label("Press Space to restart", style);
        Table winTable = new Table();
        winTable.setFillParent(true);
        p2Stage.addActor(winTable);
        winTable.add(win);
        winTable.row();
        winTable.add(restart);
        return p2Stage;
    }

    //Memory cleanup
    @Override
    public void dispose() {
        modelBatch.dispose();
        textureAtlas.dispose();
        cards.dispose();
        tableTopModel.dispose();
        skin.dispose();
        stage.dispose();
    }

    @Override
    @Deprecated
    public void resize(int width, int height) {
        if (width > height) {
            camera.viewportHeight = MINIMUM_VIEWPORT_SIZE;
            camera.viewportWidth = camera.viewportHeight * (float) width / (float) height;
        } else {
            camera.viewportWidth = MINIMUM_VIEWPORT_SIZE;
            camera.viewportHeight = camera.viewportWidth * (float) height / (float) width;
        }
        camera.update();
        stage.getViewport().update(width, height, true);
    }

    public static class CenterDeck {
        //Create card pile
        static final ArrayList<Card> centerCards = new ArrayList<Card>();

        //Add card to top of pile
        static void addCard(Card card) {
            centerCards.add(0, card);
        }

        //Add the pile to a player's hand
        public static void giveDeckToPlayer(Player player) {
            for (Card card : centerCards) {
                if (player == player1) {
                    //Reset card position and rotation
                    card.position.set(2, -1.5f, 0.1f);
                    card.angle = 180;
                    card.update();
                } else {
                    //Reset card position and rotation
                    card.position.set(-2, 1.5f, 0.1f);
                    card.angle = 180;
                    card.update();
                }
                player.playerDeck.add(card);
            }
            //Clear the center pile
            centerCards.clear();
        }

        public static void takeTwoFromPlayer(Player player) {
            //Put two of the player's cards in the center pile if possible
            if (player.playerDeck.size() > 2) {
                centerCards.add(player.playerDeck.remove(0));
                centerCards.add(player.playerDeck.remove(0));
            } else {
                centerCards.add(player.playerDeck.remove(0));
            }
        }
    }

    public static class Deck {
        //Create list of all cards in play
        private final ArrayList<Card> cards;

        //Create textures for all cards
        Deck(TextureAtlas atlas, int backIndex) {
            cards = new ArrayList<Card>();
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    Sprite front = atlas.createSprite(suit.name, rank.pointValue);
                    Sprite back = atlas.createSprite("back", backIndex);
                    cards.add(new Card(suit, rank, back, front));
                }
            }
        }

        //Shuffle cards
        void shuffle() {
            Collections.shuffle(cards);
        }

        //Gets deck size
        int getDeckSize() {
            return cards.size();
        }

        //Prints deck in human readable form
        public String toString() {
            String s = "";
            for (Card card : cards) {
                s += card + "\n";
            }
            return s;
        }
    }

    //Controls rendering of cards
    public static class CardBatch extends ObjectSet<Card> implements RenderableProvider, Disposable {
        Renderable renderable;
        Mesh mesh;
        MeshBuilder meshBuilder;

        CardBatch(Material material) {
            final int maxCards = 52;
            final int maxVerts = maxCards * 8;
            final int maxIndices = maxCards * 12;

            mesh = new Mesh(false, maxVerts, maxIndices, VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
            meshBuilder = new MeshBuilder();
            renderable = new Renderable();
            renderable.material = material;
        }

        @Override
        public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
            meshBuilder.begin(mesh.getVertexAttributes());
            meshBuilder.part("card", GL20.GL_TRIANGLES, renderable.meshPart);
            for (Card card : this) {
                meshBuilder.setVertexTransform(card.transform);
                meshBuilder.addMesh(card.vertices, card.indices);
            }
            meshBuilder.end(mesh);
            renderables.add(renderable);
        }

        @Override
        public void dispose() {
            mesh.dispose();
        }
    }
}
