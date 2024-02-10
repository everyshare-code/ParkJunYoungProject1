package addressbook;

import addressbook.bgm.AddressBookBGM;
import addressbook.layout.AddressBookLayout;

public class AddressBookApp {
	public static void main(String[] args){
		//배경음악 재생할 스레드 생성
		Thread bgm=new Thread(new AddressBookBGM());
		//데몬 설정
		bgm.setDaemon(true);
		//레이아웃 생성 및 뷰
		new AddressBookLayout();
		//배경음악 스레드 실행
		bgm.start();
	}
}