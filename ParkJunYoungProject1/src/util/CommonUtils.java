package util;

import java.util.List;
import java.util.Vector;

public class CommonUtils {
	public static final int INPUT_TYPE_INT=0;
	public static final int INPUT_TYPE_STRING=1;
	
	/*
	한글 음절은 기본적으로 초성, 중성, 종성으로 구성
    초성은 한글 음절의 첫 번째 자음
    한글 초성은 총 19개:ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ
    한글 중성은 총 21개:ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ
    종성은 총 27개이나 종성 28개(27개의 종성에 종성이 없을 때를 더해 28개)  
    :''ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ
    한글은 다음과 같은 규칙으로 유니코드값이 생성된다
    ( 초성인덱스 * 21 + 중성인덱스)*28+종성인덱스 +0xAC00
    초성 인덱스 추출:(문자유니코드-0xAC00)/28/21
    중성 인덱스 추출:(문자유니코드-0xAC00)/28%21
    종성 인덱스 추출:(문자유니코드-0xAC00)%28 */ 
	//문자를 캐릭터형으로 한문자씩 나눠서 리스트로 변환(한글일 경우 초성,중성,종성으로 나누기)
	public static List<Character> getConsonants(String value) {
		
		char[] firstInitialConsonants= {'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
		char[] middleInitialConsonants= {'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ'};
		char[] lastInitialConsonants= {' ','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
		List<Character> consonants=new Vector<Character>();
		char[] valueToChar=value.toCharArray();
		for(int i=0; i<valueToChar.length; i++) {
			char c=valueToChar[i];
			//초성만 있다면 초성만 추가
			if(c>='ㄱ'&&c<='ㅎ') {
				consonants.add(valueToChar[i]);
				continue;
			}else if(c>='가'&&c<='힣') { //초성만 있지 않은 경우 중성,종성 추가
				int firstIndex = (valueToChar[i]-'가')/28/21;//초성의 인덱스 얻기
				int middleIndex = (valueToChar[i]-'가')/28%21;//초성의 인덱스 얻기
				int lastIndex= (valueToChar[i]-'가')%28;//초성의 인덱스 얻기
				consonants.add(firstInitialConsonants[firstIndex]);
				consonants.add(middleInitialConsonants[middleIndex]);
				consonants.add(lastInitialConsonants[lastIndex]);
				continue;
			}else { //그 외 문자는 그대로 추가
				consonants.add(c);
			}
		}
	
	
		return consonants;
	}//getConsonants
	
	//조사(은,는) 판단
	public static String getPostposition(String label) {
		//종성 추출
		int lastIndex= (label.charAt(label.length()-1)-'가')%28;
		//종성이 있을 경우 은을 반환
		if(lastIndex>0) return label+"은";
		//종성이 없을 경우 는을 반환
		return label+"는";
	}//getPostposition
}
