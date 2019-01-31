package view;

import javafx.scene.media.AudioClip;

public class AudioClips {
    private AudioClip themeSong = new AudioClip(getClass().getResource("/sounds/tribal-game-theme.wav").toExternalForm());
    private AudioClip click = new AudioClip(getClass().getResource("/sounds/click.wav").toExternalForm());
    private AudioClip introSong = new AudioClip(getClass().getResource("/sounds/intro.wav").toExternalForm());
    private AudioClip devSound = new AudioClip(getClass().getResource("/sounds/dev-card.wav").toExternalForm());
    private AudioClip buildSound = new AudioClip(getClass().getResource("/sounds/building.wav").toExternalForm());
    private AudioClip marchSound = new AudioClip(getClass().getResource("/sounds/marching.wav").toExternalForm());
    private AudioClip robberSound = new AudioClip(getClass().getResource("/sounds/robber.wav").toExternalForm());
    private AudioClip diceSound = new AudioClip(getClass().getResource("/sounds/dice.wav").toExternalForm());
    private AudioClip knightSound = new AudioClip(getClass().getResource("/sounds/knight.wav").toExternalForm());
    private AudioClip loseSound = new AudioClip(getClass().getResource("/sounds/lose.wav").toExternalForm());
    private AudioClip vicSound = new AudioClip(getClass().getResource("/sounds/victiory_point.wav").toExternalForm());
    private AudioClip winSound = new AudioClip(getClass().getResource("/sounds/win.wav").toExternalForm());
    private AudioClip roadSound = new AudioClip(getClass().getResource("/sounds/longest_road.mp3").toExternalForm());

    public AudioClips(){

    }

    public AudioClip getThemeSong() {
        return themeSong;
    }

    public AudioClip getClick() {
        return click;
    }

    public void setClick(AudioClip click) {
        this.click = click;
    }

    public AudioClip getIntroSong() {
        return introSong;
    }

    public AudioClip getDevSound() {
        return devSound;
    }

    public AudioClip getBuildSound() {
        return buildSound;
    }

    public AudioClip getMarchSound() {
        return marchSound;
    }

    public AudioClip getRobberSound() {
        return robberSound;
    }

    public AudioClip getDiceSound() {
        return diceSound;
    }

    public AudioClip getKnightSound() {
        return knightSound;
    }

    public AudioClip getLoseSound() {
        return loseSound;
    }

    public AudioClip getVicSound() {
        return vicSound;
    }

    public AudioClip getWinSound() {
        return winSound;
    }

    public AudioClip getRoadSound() {
        return roadSound;
    }
}
