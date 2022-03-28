package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
	final Drop game;
	SpriteBatch batch;
	Texture background;
	Array<Texture> weaponsTextures = new Array<>();
	Array<Texture> shoulderStraps = new Array<>();
	Array<Texture> vadimSprites = new Array<>();
	Texture shoulderStrap;
	Sound caughtSound;
    Sound escapeSound;
	Music music;
	OrthographicCamera camera;
	ImprovedRectangle vadim;
	Array<ImprovedRectangle> raindrops = new Array<>();
	Random random;
	Stage stage;
	TextButton.TextButtonStyle textButtonStyle;
	Skin skin;
	BitmapFont font;
	TextButton left;
	Button right;
	Button space;
	long lastDropTime;
	int caughtGrenades = 0;
	int caughtAks = 0;
	int caughtBazookas = 0;
	int escapedGrenades = 0;
	int escapedAks = 0;
	int escapedBazookas = 0;
    int score = 0;
	long startCurrentTime;
	long finishCurrentTime;
	float shoulderStrapX;
	boolean isPressed;


	public GameScreen(final Drop gam) {
		this.game = gam;

		isPressed = false;

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		skin = new Skin();
		font = new BitmapFont();
		font.getData().setScale(15);
		textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font;

		left = new TextButton("<", textButtonStyle);
		stage.addActor(left);
		left.setPosition(80, 20);

		right = new TextButton(">", textButtonStyle);
		stage.addActor(right);
		right.setPosition(1900, 20);

		space = new TextButton("(0)", textButtonStyle);
		stage.addActor(space);
		space.setPosition(80, 420);



		random = new Random();
		background = new Texture("pictures/background.jpg");

		vadimSprites.add(new Texture(Gdx.files.internal("pictures/vadim_l.png")));
		vadimSprites.add(new Texture(Gdx.files.internal("pictures/vadim_r.png")));

		weaponsTextures.add(new Texture(Gdx.files.internal("pictures/grenade.png")));
		weaponsTextures.add(new Texture(Gdx.files.internal("pictures/ak-47.png")));
		weaponsTextures.add(new Texture(Gdx.files.internal("pictures/bazooka.png")));

		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/soldier.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/corporal.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/second_sergeant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/sergeant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/senior_sergeant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/sergeant-major.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/ensign.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/senior_ensign.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/second_lieutenant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/lieutenant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/senior_lieutenant.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/captain.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/major.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/lieutenant-colonel.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/colonel.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/major-general.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/lieutenant-general.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/colonel-general.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/army_general.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/marshal.png")));
		shoulderStraps.add(new Texture(Gdx.files.internal("pictures/shoulder_straps/generalissimo.png")));


		caughtSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick.wav"));
        escapeSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.wav"));


		if(random.nextInt(100) < 5) {
			music = Gdx.audio.newMusic(Gdx.files.internal("music/youth_in_boots_g.mp3"));
		}
		else {
			music = Gdx.audio.newMusic(Gdx.files.internal("music/youth_in_boots.mp3"));
		}
		music.setLooping(false);
		music.setVolume(0.2f);
		music.play();


		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

        spawnVadim();
		spawnRaindrop();

		startCurrentTime = System.currentTimeMillis();
	}

	@Override
	public void render (float delta) {
		isPressed = false;

		if(score > 100 && score < 201){shoulderStrap = shoulderStraps.get(1);}
		else if (score > 200 && score < 301){shoulderStrap = shoulderStraps.get(2);}
		else if (score > 300 && score < 401){shoulderStrap = shoulderStraps.get(3);}
		else if (score > 400 && score < 501){shoulderStrap = shoulderStraps.get(4);}
		else if (score > 500 && score < 601){shoulderStrap = shoulderStraps.get(5);}
		else if (score > 600 && score < 701){shoulderStrap = shoulderStraps.get(6);}
		else if (score > 700 && score < 801){shoulderStrap = shoulderStraps.get(7);}
		else if (score > 800 && score < 901){shoulderStrap = shoulderStraps.get(8);}
		else if (score > 900 && score < 1001){shoulderStrap = shoulderStraps.get(9);}
		else if (score > 1000 && score < 1201){shoulderStrap = shoulderStraps.get(10);}
		else if (score > 1200 && score < 1401){shoulderStrap = shoulderStraps.get(11);}
		else if (score > 1400 && score < 1601){shoulderStrap = shoulderStraps.get(12);}
		else if (score > 1600 && score < 1801){shoulderStrap = shoulderStraps.get(13);}
		else if (score > 1800 && score < 2001){shoulderStrap = shoulderStraps.get(14);}
		else if (score > 2000 && score < 2401){shoulderStrap = shoulderStraps.get(15);}
		else if (score > 2400 && score < 2801){shoulderStrap = shoulderStraps.get(16);}
		else if (score > 2800 && score < 3001){shoulderStrap = shoulderStraps.get(17);}
		else if (score > 3000 && score < 3501){shoulderStrap = shoulderStraps.get(18);}
		else if (score > 3500 && score < 4001){shoulderStrap = shoulderStraps.get(19);}
		else if (score > 4000){shoulderStrap = shoulderStraps.get(20);}
		else {shoulderStrap = shoulderStraps.get(0);}

		if(vadim.direction){
			vadim.texture = vadimSprites.get(1);
			shoulderStrapX = -5;
		}
		else {
			vadim.texture = vadimSprites.get(0);
			shoulderStrapX = 85;
		}


		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		game.batch.setProjectionMatrix(camera.combined);

		game.batch.begin();
		game.batch.draw(background, 0, 0);
		game.batch.draw(vadim.texture, vadim.x, vadim.y);
		game.batch.draw(shoulderStrap, vadim.x + shoulderStrapX, vadim.y + 40);

		for(ImprovedRectangle raindrop: raindrops) {
			game.batch.draw(raindrop.texture, raindrop.x, raindrop.y);
		}


		if (finishCurrentTime - startCurrentTime > 186000){
			game.font.draw(game.batch, "Press the space bar to resign", 50, 430);

			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
				raindrops.clear();
				music.stop();
				weaponsTextures.clear();
				vadimSprites.clear();
				shoulderStraps.clear();
				game.setScreen(new MainMenuScreen(game));
			}


			space.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					if(!isPressed) {
						isPressed = true;
						raindrops.clear();
						music.stop();
						weaponsTextures.clear();
						vadimSprites.clear();
						shoulderStraps.clear();
						game.setScreen(new MainMenuScreen(game));
					}
				}
			});
		}

		game.font.draw(game.batch, "score: " + score, 50, 470);

		game.batch.end();

		if(game.isPhone) {
			stage.draw();
		}


		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			vadim.x -= 500 * Gdx.graphics.getDeltaTime();
			vadim.direction = false;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			vadim.x += 500 * Gdx.graphics.getDeltaTime();
			vadim.direction = true;
		}

		if(vadim.x < 0){
			vadim.x = 0;}
		if(vadim.x > 800 - 64){
			vadim.x = 800 - 64;}

		if(TimeUtils.nanoTime() - lastDropTime > 1000000000){
			spawnRaindrop();}

		Iterator<ImprovedRectangle> iterator = raindrops.iterator();
		while(iterator.hasNext()) {
			ImprovedRectangle raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0){
				switch (raindrop.type){
					case "grenade":
						escapedGrenades++;
						break;
					case "ak-47":
						escapedAks++;
						break;
					case "bazooka":
						escapedBazookas++;
						break;
				}
                escapeSound.play();
				iterator.remove();
			}
			if(raindrop.overlaps(vadim)) {
				switch (raindrop.type){
					case "grenade":
						caughtGrenades++;
						break;
					case "ak-47":
						caughtAks++;
						break;
					case "bazooka":
						caughtBazookas++;
						break;
				}
				caughtSound.play();
				iterator.remove();
			}
		}
		calculateScore();

		finishCurrentTime = System.currentTimeMillis();



		if(!music.isPlaying()){
			if(random.nextInt(100) < 5) {
				music = Gdx.audio.newMusic(Gdx.files.internal("music/youth_in_boots_g.mp3"));
			}
			else {
				music = Gdx.audio.newMusic(Gdx.files.internal("music/youth_in_boots.mp3"));
			}
			music.setVolume(0.2f);
			music.play();
		}



		right.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(!isPressed) {
					isPressed = true;
					vadim.x += 4000 * Gdx.graphics.getDeltaTime();
					vadim.direction = true;
				}
			}
		});

		left.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				if(!isPressed) {
					isPressed = true;
					vadim.x -= 4000 * Gdx.graphics.getDeltaTime();
					vadim.direction = false;
				}
			}
		});


	}
	
	@Override
	public void dispose () {
		caughtSound.dispose();
		music.dispose();
		batch.dispose();
		background.dispose();
	}

	private void spawnRaindrop() {
		ImprovedRectangle raindrop = new ImprovedRectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;

		Random random = new Random();
		int randomObjectTexture = random.nextInt(3);
		switch (randomObjectTexture){
			case 0:
				raindrop.texture = weaponsTextures.get(0);
				raindrop.type = "grenade";
				raindrop.width = 32;
				raindrop.height = 32;
				break;
			case 1:
				raindrop.texture = weaponsTextures.get(1);
				raindrop.type = "ak-47";
				raindrop.width = 101;
				raindrop.height = 32;
				break;
			case 2:
				raindrop.texture = weaponsTextures.get(2);
				raindrop.type = "bazooka";
				raindrop.width = 101;
				raindrop.height = 32;
				break;
		}

		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

    private void spawnVadim() {
        vadim = new ImprovedRectangle();
        vadim.x = 400 - 32;
        vadim.y = 0;
        vadim.width = 110;
        vadim.height = 100;
        vadim.direction = false;
    }

	@Override public void resize(int width, int height) {}
	@Override public void show(){}
	@Override public void hide(){}
	@Override public void pause(){}
	@Override public void resume(){}

	private void calculateScore(){
        score = caughtGrenades + caughtAks *10 + caughtBazookas *25 -
                        escapedGrenades *10 - escapedAks *25 - escapedBazookas *100;
    }
}
