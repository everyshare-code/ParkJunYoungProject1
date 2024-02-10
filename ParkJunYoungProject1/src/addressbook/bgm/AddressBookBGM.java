package addressbook.bgm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class AddressBookBGM extends PlaybackListener implements Runnable{
	private File file;
	private FileInputStream fis;
	private AdvancedPlayer player;
	
	public AddressBookBGM() {
		//파일 객체 생성
		file=new File("music/music.mp3");
		//플레이어 설정
		setPlayer();
	}
	
	public void setPlayer() {
		try {
			//파일 읽어오기
			fis=new FileInputStream(file);
			//플레이어 객체 생성
			player=new AdvancedPlayer(fis);
			//플레이어 리스너 설정
			player.setPlayBackListener(this);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void playbackFinished(PlaybackEvent evt) { //player 재생이 끝나면 이벤트 발생
		try {
			//스트림 닫기
			if(fis!=null) fis.close();
			if(player!=null)player.close();
			//플레이어 새로 생성
			setPlayer();
			//플레이어 시작
			player.play();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		try {
			player.play();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
}
