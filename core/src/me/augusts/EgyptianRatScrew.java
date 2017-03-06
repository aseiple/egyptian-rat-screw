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
    private ModelBatch modelBatch;
	private TextureAtlas textureAtlas;
    private Skin skin;
    private Stage stage;

    static boolean gameover = false;

    private Label p1Label;
    private Label p2Label;

	final static float CARD_WIDTH = 1f;
	final static float CARD_HEIGHT = CARD_WIDTH * 277f / 200f;
	private final static float MINIMUM_VIEWPORT_SIZE = 5f;

	private OrthographicCamera camera;
    private static Deck deck;
	private static CardBatch cards;

    private Model tableTopModel;
    private ModelInstance tableTop;
    private Environment environment;

    static Player player1;
    static Player player2;

    static Status player1Status;
    static Status player2Status;

	@Override
	public void create () {
	    stage = new Stage();
	    skin = new Skin();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

	    Table table = new Table();
        table.setFillParent(true);
	    stage.addActor(table);

        p1Label = new Label("21 Cards left", skin);
        p2Label = new Label("21 Cards left", skin);
	    table.add(p1Label).padBottom(375).padRight(200);
	    table.row();
	    table.add(p2Label).padLeft(200);

        InputHandler inputHandler = new InputHandler();
	    Gdx.input.setInputProcessor(inputHandler);
        modelBatch = new ModelBatch();
        textureAtlas = new TextureAtlas("core/assets/carddeck.atlas");
        Material material = new Material(
                TextureAttribute.createDiffuse(textureAtlas.getTextures().first()),
                new BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f));
        cards = new CardBatch(material);

        deck = new Deck(textureAtlas, 3);
        deck.shuffle();
        player1 = new Player();
        player1Status = Status.Deal;
        player2 = new Player();
        player2Status = Status.Waiting;

        dealCards();

        camera = new OrthographicCamera();
        camera.position.set(0, 0, 60);
        camera.lookAt(0, 0, 0);

        ModelBuilder builder = new ModelBuilder();
        builder.begin();
        builder.node().id = "top";
        //noinspection deprecation
        builder.part("top", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(new Color(0x63750A))))
                .box(0f, 0f, -0.5f, 20f, 20f, 1f);
        tableTopModel = builder.end();
        tableTop = new ModelInstance(tableTopModel);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f));
	}

	static void dealCards() {
	    player1.playerDeck.clear();
	    player2.playerDeck.clear();
	    CenterDeck.centerCards.clear();
        for(Card card : deck.cards) {
            if(player1.playerDeck.size() < deck.getDeckSize() / 2) {
                player1.playerDeck.add(card);
                card.position.set(2, -1.5f, player1.playerDeck.size() + 1);
                card.angle = 180;
                card.update();
                cards.add(card);
            }
            else {
                player2.playerDeck.add(card);
                card.position.set(-2, 1.5f, player2.playerDeck.size() + 1);
                card.angle = 180;
                card.update();
                cards.add(card);
            }
        }
    }

	@Override
	public void render () {
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        modelBatch.begin(camera);
        modelBatch.render(tableTop, environment);
        if(player1.playerDeck.size() == 0) {
            modelBatch.end();
            player2Wins().draw();
            gameover = true;
        }
        else if(player2.playerDeck.size() == 0) {
            modelBatch.end();
            player1Wins().draw();
            gameover = true;
        }
        else {
            modelBatch.render(cards, environment);
            modelBatch.end();

            if(player1Status == Status.Deal) {
                p1Label.setText(player1.playerDeck.size() + " Cards left" + "\n It's your turn");
                p2Label.setText(player2.playerDeck.size() + " Cards left");
            }
            else {
                p1Label.setText(player1.playerDeck.size() + " Cards left");
                p2Label.setText(player2.playerDeck.size() + " Cards left" + "\n It's your turn");
            }
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
	
	@Override
	public void dispose () {
        modelBatch.dispose();
        textureAtlas.dispose();
        cards.dispose();
        tableTopModel.dispose();
        skin.dispose();
        stage.dispose();
	}

	@Override
    public void resize(int width, int height) {
	    if(width > height) {
	        camera.viewportHeight = MINIMUM_VIEWPORT_SIZE;
	        camera.viewportWidth = camera.viewportHeight * (float)width / (float)height;
        }
        else {
	        camera.viewportWidth = MINIMUM_VIEWPORT_SIZE;
	        camera.viewportHeight = camera.viewportWidth * (float)height / (float)width;
        }
        camera.update();
	    stage.getViewport().update(width, height, true);
    }

    public static class CenterDeck {
	    static final ArrayList<Card> centerCards = new ArrayList<Card>();

        static void addCard(Card card) {
	        centerCards.add(0, card);
        }

        public static void giveDeckToPlayer(Player player) {
	        for (Card card : centerCards) {
	            if(player == player1) {
                    card.position.set(2, -1.5f, 0.1f);
                    card.angle = 180;
                    card.update();
                }
                else {
                    card.position.set(-2, 1.5f, 0.1f);
                    card.angle = 180;
                    card.update();
                }
	            player.playerDeck.add(card);
            }
            centerCards.clear();
        }

        public static void takeTwoFromPlayer(Player player) {
            if(player.playerDeck.size() > 2) {
                centerCards.add(player.playerDeck.remove(0));
                centerCards.add(player.playerDeck.remove(0));
            }
            else {
                centerCards.add(player.playerDeck.remove(0));
            }
        }
    }

    public static class Deck {
	    private final ArrayList<Card> cards;

	    Deck(TextureAtlas atlas, int backIndex) {
	        cards = new ArrayList<Card>();
	        for(Suit suit : Suit.values()) {
	            for(Rank rank : Rank.values()) {
	                Sprite front = atlas.createSprite(suit.name, rank.pointValue);
	                Sprite back = atlas.createSprite("back", backIndex);
	                cards.add(new Card(suit, rank, back, front));
                }
            }
        }

        void shuffle() {
	        Collections.shuffle(cards);
        }

        int getDeckSize() {
	        return cards.size();
        }

        public String toString() {
	        String s = "";
	        for(Card card : cards) {
	            s += card + "\n";
            }
            return s;
        }
    }

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
	        for(Card card : this) {
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
