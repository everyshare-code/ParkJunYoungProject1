package addressbook.controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import addressbook.layout.AddressBookDialog;
import addressbook.layout.AddressBookLayout;
import addressbook.model.Address;
import util.CommonUtils;



public class AddressBookHandler extends WindowAdapter implements ActionListener, KeyListener, MouseListener{
	//AddressBookLayout 필드
	private DefaultTableModel tableModel;
	private JTable table;
    private JComboBox<String> sortComboBox;
    private JComboBox<String> searchComboBox;
    private JTextField searchTextField;
    private JButton addButton;
    private JButton exportButton;
    private JButton deleteButton;
    private AddressBookLayout frame;
    private JLabel dataSizeLabel;
	
    //AddressBookDialog 필드
    private AddressBookDialog dialog;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField addrField;
    private JTextField telField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private List<Address> addressList; //기존 주소록 데이터
    private List<Address> searchAddressList; //검색된 주소록 데이터
    private List<Character> searchTextFieldToChar; //검색 데이터 캐릭터형 리스트 컬렉션
    private int editIndex; //수정할 객체의 리스트 인덱스
    private int searchSelectIndex;// 선택된 검색 방식 
    private final int SEARCH_BY_NAME=0; //이름으로 검색
    private final int SEARCH_BY_ADDR=1; //주소로 검색
    private final int SEARCH_BY_TEL=2; //전화번호로 검색
    private StringBuffer value; //텍스트필드 유효성 검사를 위해 문자열을 담을 객체
    private File addressDataFile; //주소록 리스트 컬렉션 객체 저장된 파일
	private int selectedColumn=-1; //선택된 컬렘 인덱스 저장
	private int orderType=Address.ORDER_BY_ASCENDING; //오름차순, 내림차순 정렬 방식
    public AddressBookHandler(AddressBookLayout frame) {
		//AddressBookDialog의 부모로 설정하기 위해 전달 받음
		this.frame=frame;
		//기존 주소록 데이터를 전달 받을 리스트 컬렉션 생성
		addressList=new Vector<Address>();
		//검색 데이터 캐릭터형 리스트 컬렉션 생성
		searchTextFieldToChar=new Vector<Character>();
		//searchTextFiled 문자열을 위한 객체 생성
		value=new StringBuffer();
		
		//파일로 저장된 데이터 파일 객체에 담기
		addressDataFile=new File("data/AddressData.dat");
		
		//JOptionPane 예,아니오,취소 버튼 텍스트 한글로 설정
		UIManager.put("OptionPane.yesButtonText", "예");
		UIManager.put("OptionPane.noButtonText", "아니오");
		UIManager.put("OptionPane.cancelButtonText", "취소");
		
		
		
	}//생성자
	
	//버튼,콤보박스 이벤트
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==addButton) { // 주소록 추가 버튼
			//다이얼로그 생성
			dialog=new AddressBookDialog(frame, "주소록 추가", null, this);
			dialog.setVisible(true);
		}else if(e.getSource()==deleteButton) { //삭제 버튼 
			deleteAddress();
		}else if(e.getSource()==exportButton){ //내보내기 버튼
			exportAddress();
		}else if(e.getSource()==saveButton) { //다이얼로그 저장 버튼
			//수정,저장 완료시 다이얼로그 종료    
			if(saveAddress()) exitDialog();
		}else if(e.getSource()==cancelButton) { //다이얼로그 취소 버튼
			//다이얼로그 종료
			exitDialog();
		}else if(e.getSource()==sortComboBox) { //정렬 콤보박스 이벤트 발생
			//리스트 컬렉션, 테이블 모델 재정렬
			//sortTable(addressList);
		}else if(e.getSource()==searchComboBox) { //검색 콤보 박스 정렬 이벤트 발생
			//searchTextField 초기화
			searchTextField.setText("");
			//선택된 인덱스로 검색 방식 설정
			searchSelectIndex=searchComboBox.getSelectedIndex();
			
		}
	}//actionPerformed
	
	//프로그램 종료시
	@Override
	public void windowClosing(WindowEvent e) {
		if(e.getSource()==frame) {//프로그램 종료시 리스트 컬렉션 저장
			//종료 확인
			int confirmNumber=getConfirmNumber("종료");
			//종료 예 버튼 클릭시
			if(confirmNumber==0) {
				//파일 저장
				saveAddressToFile();
				//프로그램 종료
				System.exit(0);
			}
		}else if(e.getSource()==dialog) { //다이얼로그 x버튼 클릭시
			//다이얼로그 종료
			exitDialog();
		}
	}//windowClosing
	
	@Override
	public void keyTyped(KeyEvent e) {
		//다이얼로그 텍스트필드에 키 이벤트 발생
		if(e.getSource()==nameField||e.getSource()==ageField
				||e.getSource()==addrField||e.getSource()==telField) { 
			//유효성 검사
			characterValidate(e);
		}
	}//keyTyped

	@Override
	public void keyPressed(KeyEvent e) {
	}//keyPressed

	@Override
	public void keyReleased(KeyEvent e) { //키를 눌렀다 뗐을 경우(keyPressed,keyTyped는 호출 시점이 정확하지 않아서 keyReleased활용)
		//searchTextField에 이벤트 발생
		if(e.getSource()==searchTextField) {
			//searchTextField에 입력된 값을 캐릭터형 리스트로 변경
			searchTextFieldToChar=CommonUtils.getConsonants(searchTextField.getText());
			//찾는 값의 캐릭터형 리스트와 기존 데이터의 캐릭터형 리스트와 비교 후 동일한 데이터를 리스트로 받음
			searchAddressList=searchAddress(searchTextFieldToChar);
			//테이블 모델 전달 받은 리스트로 재정렬
			sortTable(searchAddressList,selectedColumn,orderType);
		}
		
	}//keyReleased
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}//mouseClicked

	@Override
	public void mousePressed(MouseEvent e) {
		//테이블 행 더블 클릭시 수정 다이얼로그 띄우기
		if(e.getClickCount()==2&&e.getSource()==table) {
			//수정할 Address 객체 담기
			Address editAddress=editAddress();
			//다이얼로그 생성,Address 객체 다이얼로그로 전달
			if(editAddress!=null) {
				dialog=new AddressBookDialog(frame, "수정", editAddress, this);
				dialog.setVisible(true);
			}
		}	
	}//mousePressed

	@Override
	public void mouseReleased(MouseEvent e) {
		//테이블 헤더 클릭시 
		if(e.getSource()==table.getTableHeader()) {
			//현재 선택된 컬럼 번호 +1
			int currentSelectColumn=table.getTableHeader().columnAtPoint(e.getPoint())+1;
			//저장된 컬럼 번호랑 다를 경우
			if(selectedColumn!=currentSelectColumn) {
				//현재 선택된 컬럼 번호 세팅
				selectedColumn=currentSelectColumn;
				//오름차순 세팅
				orderType=Address.ORDER_BY_ASCENDING;
			}else {
				//오름차순일 때 클릭했을 경우 내림차순
				if(orderType==Address.ORDER_BY_ASCENDING) orderType=Address.ORDER_BY_DESCENDING;
				//내림차순일 때 클릭했을 경우 오름 차순
				else orderType=Address.ORDER_BY_ASCENDING;
			}
			//검색중이 아닐 경우 기본 리스트로 정렬
			if(searchTextField.getText().isEmpty())
				sortTable(addressList,selectedColumn, orderType);
			else //검색중일 경우 검색된 리스트로 정렬
				sortTable(searchAddressList,selectedColumn, orderType);
		}
	}//mouseReleased

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}//mouseEntered

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}//mouseExited
	
	//CSV로 내보내기 
	private void exportAddress() {
		BufferedOutputStream bos=null;
		FileOutputStream fos=null;
		File exportFile=null;
		//사용자가 선택한 파일명
		String userSetFileName=null;
		
		//데이터를 저장할 위치를 선택할 수 있는 객체 생성
		JFileChooser chooser=new JFileChooser();
		//저장 시간 포맷 설정
		SimpleDateFormat sdf=new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH_mm_ss");
		//기본 파일명 - 주소록 데이터(저장시간).csv
		String exportFileName="주소록 데이터("+sdf.format(new Date().getTime())+")";
		//기본 파일명으로 파일 객체 생성
		exportFile=new File(exportFileName);
		//기본 파일명으로 파일 설정
		chooser.setSelectedFile(exportFile);
		//csv만 저장 되도록 확장자 설정
		FileNameExtensionFilter filter=new FileNameExtensionFilter("CSV (*.csv)","csv");
		chooser.setFileFilter(filter);
		//저장할 위치를 선택할 다이얼로그 띄우기
		int selected=chooser.showSaveDialog(frame);
		//폴더가 선택됐다면
		if(selected==JFileChooser.APPROVE_OPTION) {
			try {
				//사용자가 입력한 파일명 (파일명과 경로를 변경할 수도 있기 때문에)
				userSetFileName=chooser.getSelectedFile().getAbsolutePath();
				//파일 확장자 입력을 하지 않았거나 확장자가 csv가 아닌 경우 확장자 csv로 설정
				if(!userSetFileName.endsWith(".CSV")&&!userSetFileName.endsWith(".csv"))
					userSetFileName+=".csv";
				
				//스트림 준비
				fos=new FileOutputStream(exportFile);
				bos=new BufferedOutputStream(fos);
				
				//헤더 저장
				String header="이름,나이,주소,전화번호\r\n";
				
				bos.write(header.getBytes());
				StringBuffer bf=new StringBuffer();
				//데이터 저장
				for(Address address:addressList) {
					// ,(콤마)로 구분한 한줄 데이터
					bf.append(address.getName());
					bf.append(",");
					bf.append(address.getAge());
					bf.append(",");
					bf.append(address.getAddr());
					bf.append(",");
					bf.append(""+address.getTel());
					bf.append("\r\n");
					
					//내보내기
					bos.write(bf.toString().getBytes());
					bf.delete(0, bf.toString().length());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					//스트림 닫기
					if(bos!=null) {
						bos.flush();
						bos.close();
					}
					if(fos!=null) fos.close();
					//파일 생성후 사용자가 선택한 경로로 이동 및 파일명 변경
					Files.move(exportFile.toPath(), Paths.get(userSetFileName), StandardCopyOption.REPLACE_EXISTING);
					//파일이 저장된 폴더 열기
					Desktop.getDesktop().open(new File(userSetFileName.substring(0,userSetFileName.lastIndexOf("/"))));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			//파일 저장 경로 메시지 다이얼로그
			//JOptionPane.showMessageDialog(frame, userSetFileName+"저장 완료");
			
		}
	}
	
	
	//수정 버튼 이벤트 메소드
	private Address editAddress() {
		//선택된 행 인덱스
		editIndex=table.getSelectedRow();
		// 선택된 연락처가 없을 경우
		if(editIndex==-1) {
			JOptionPane.showMessageDialog(frame, "수정할 주소록을 선택해주세요.");
			return null;
		}
		return addressList.get(editIndex);
	}//editAddress
	
	//삭제 버튼 이벤트 메소드
	private void deleteAddress() {
		// 선택된 요소 인덱스
		int deleteIndex=table.getSelectedRow();
		// 선택된 요소가 없으면
		if(deleteIndex==-1) {
			JOptionPane.showMessageDialog(frame, "삭제할 주소록을 선택해주세요.");
			return;
		}
		//삭제 확인
		int confirmNumber=getConfirmNumber("삭제");
		//예 버튼 클릭시
		if(confirmNumber==0) {
			// 리스트 컬렉션에 객체 삭제
			addressList.remove(deleteIndex);
			// 테이블 모델에 객체 삭제
			tableModel.removeRow(deleteIndex);
		}
		//테이블 데이터 세팅 및 정렬
		sortTable(addressList,selectedColumn,orderType);
	}//deleteAddress
	
	
	//다이얼로그 저장 버튼 이벤트 메소드
	private boolean saveAddress() {
		// 유효성 검사, 테이블 모델에 넣을 배열 준비
		Object[] row= emptyValueCheck(nameField.getText().trim(), 
										ageField.getText().trim(), 
										addrField.getText().trim(), 
										telField.getText().trim());
		if(row==null) return false;
		Address address=null;
		if(!dialog.isEdit()) {	//주소록 추가일 경우
			//리스트 컬렉션에 객체 추가
			address=new Address(row[0].toString(),Integer.parseInt(row[1].toString()),
					row[2].toString(),row[3].toString());
			addressList.add(address);
			
		}else { //수정일 경우
			//텍스트 필드의 값이 변경 됐을 경우
			if(editValueCheck(row)) {
				//수정 확인
				int confirmNumber=getConfirmNumber("수정");
				if(confirmNumber==0) {
					//리스트 컬렉션 객체 수정
					address=new Address(row[0].toString(),
										Integer.parseInt(row[1].toString()),
										row[2].toString(),
										row[3].toString());
					addressList.set(editIndex,address);
				}
			}
		}
		
		if(address!=null) {
			//테이블 데이터 세팅 및 정렬
			sortTable(addressList,selectedColumn,orderType);
			//추가된 테이블 포커스
			int idx=addressList.indexOf(address);
			table.setRowSelectionInterval(idx, idx);
		}
		return true;
		
	}//saveAddress
	
	//다이얼로그 수정 버튼 클릭 후 텍스트 필드의 값이 변경됐는지 확인
	private boolean editValueCheck(Object[] row) {
		Address editAddress=addressList.get(editIndex);
		String name=row[0].toString().trim();
		String age=row[1].toString().trim();
		String addr=row[2].toString().trim();
		String tel=row[3].toString().trim();
		//값이 변경 됐는지 확인
		if(!name.equals(editAddress.getName())) return true;
		else if(!age.equals(String.valueOf(editAddress.getAge()))) return true;
		else if(!addr.equals(editAddress.getAddr())) return true;
		else if(!tel.equals(editAddress.getTel())) return true;
		return false;
	}//editValueCheck
	
	

	//주소록 리스트 컬렉션 파일로 저장
	private void saveAddressToFile() {
		ObjectOutputStream oos=null;
		try {
			if(!addressList.isEmpty()) {
				oos=new ObjectOutputStream(new FileOutputStream(addressDataFile));
				oos.writeObject(addressList);
				oos.close();
			}
		}catch(IOException exception) {
			exception.printStackTrace();
		}
	}//saveAddressToFile
	
	//다이얼로그 종료
	private void exitDialog() {
		 //다이얼로그 종료
		 dialog.dispose();
		 //null로 초기화 시켜서 메모리 해제
		 nameField=null;
		 ageField=null;
		 addrField=null;
		 telField=null;
		 saveButton=null;
		 cancelButton=null;
		 dialog=null;
	}//exitDialog
	
	//테이블 모델에 넣을 Object 배열로 변경 및 다이얼로그에서 값 입력 시 빈값 확인
	private Object[] emptyValueCheck(String name, String age, String addr, String tel) {
		String emptyValue=null;
		//다이얼로그 값 입력시
		if(dialog!=null) {
			//빈값 확인 isEmpty()는 null과 ""(빈값) 감지
			if(name.isEmpty()) {
				emptyValue="이름";
			}else if(age.isEmpty()) {
				emptyValue="나이";
			}else if(addr.isEmpty()) {
				emptyValue="주소";
			}else if(tel.isEmpty()) {
				emptyValue="전화번호";
			}
			//빈 값이 있을 경우
			if(emptyValue!=null) {
				JOptionPane.showMessageDialog(dialog, emptyValue+" 필수 입력"); //메시지 다이얼로그
				//빈값이 있는 텍스트필드에 포커스
				switch (emptyValue) {
					case "이름": 
						nameField.requestFocus();
						break;
					case "나이": 
						ageField.requestFocus();
						break;
					case "주소": 
						addrField.requestFocus();
						break;
					case "전화번호": 
						telField.requestFocus();
						break;
				}
				return null;
			}else {
				//이름 한글 형식 아닐 경우 자음만 있는 경우를 배제
				if(!name.matches("[가-힣]+")) {
					JOptionPane.showMessageDialog(null, "이름을 정확하게 입력해주세요");
					//해당 텍스트 필드로 포커스 이동
					nameField.requestFocus();
					return null;
				}
				//주소에 한글,영어,숫자,(,),- 만 허용
				else if(!addr.matches("[가-힣a-zA-Z0-9\\-\\(\\)\s]+")) {
					JOptionPane.showMessageDialog(null, "주소를 정확하게 입력해주세요.");
					//해당 텍스트 필드로 포커스 이동
					addrField.requestFocus();
					return null;
				}
			}
		}
		Object[] row= {name,age,addr,tel};
		return row;
	}//emptyValueCheck
	
	//문자 숫자 유효성 검사
	private void characterValidate(KeyEvent e){
		
		//이벤트 발생된 텍스트 필드 객체
		JTextField textField = (JTextField)e.getComponent();
		
		//텍스트 필드 객체의 이름
		String label=textField.getName();
		
		//문자열 담기전 초기화
		if(!value.isEmpty())
			value.delete(0, value.length());
		
		//텍스트 필드의 문자열 담기(공백 제거)
		value.append(textField.getText().trim());
		//입력된 문자
		char c=e.getKeyChar();
		
		//esc키 누르면 다이얼로그 종료
		if(c==27) {
			exitDialog();
			return;
		}
		
		//입력중인 텍스트 필드별로 분류
		switch(label) {
			case "이름":
				/*
				//입력된 값이 세글자 이상이면 무시
				if(value.length()>3) {
					e.consume();
				}
				*/
				//값이 입력된 상태에서 엔터키를 누르면 다음 텍스트 필드로 이동
				if(!value.isEmpty()&&c=='\n') ageField.requestFocus();
				//한글 외 문자 입력시 무시
				if(!(c>='가'&&c<='힣'||c>='ㄱ'&&c<='ㅎ')) {
					e.consume();
				}
				break;
			case "전화번호":
				//10글자 이상이면 무시
				if(value.length()>10) {
					e.consume();
				}
				//값이 입력된 상태에서 엔터키 입력시 저장 버튼 클릭 이벤트 발생
				if(!value.isEmpty()&&c=='\n') saveButton.doClick();
				//숫자가 아닐 경우 무시
				if(!Character.isDigit(c)) e.consume();
				break;
			case "나이":
				//값이 입력된 상태에서 엔터키 입력시 다음 텍스트 필드로 이동
				if(!value.isEmpty()&&c=='\n') addrField.requestFocus();
				//숫자가 아닐 경우 무시
				if(!Character.isDigit(c)) e.consume();
				//첫번째 숫자 0 입력 무시
				if(value.length()==0&&c=='0') e.consume();
				//세글자 이상 입력시 무시
				else if(value.length()>=3) e.consume();
				break;
			case "주소":
				if(!value.isEmpty()&&c=='\n') telField.requestFocus();
				break;
		}
	}
	
	
	/*
	 * 전달받은 캐릭터형 리스트와 기존의 데이터를 캐릭터형 리스트로 변환해서 일치하는 값이 있는지
	 * 확인하고 일치하는 객체로 새로운 리스트를 만들어서 전달
	 * CommonUtils에 getConsonants 메소드 참고
	 */
	private List<Address> searchAddress(List<Character> searchToChar) {
		//검색되는 새로운 데이터를 담을 리스트 컬렉션 생성
		List<Address> searchAddressList=new Vector<Address>();
		//검색할 데이터의 리스트가 비었으면 기존 데이터를 전달
		if(searchToChar.size()==0) return addressList;
		//기존 데이터에서 검색
		for(Address address:addressList) {
			boolean isName=false;
			//searchSelectedComboBox의 선택 요소에 따라서 비교할 문자열 선택
			String value=switch (searchSelectIndex) {
				case SEARCH_BY_NAME: 
					isName=true;
					yield address.getName();
				
				case SEARCH_BY_ADDR: 
					yield address.getAddr();
				
				case SEARCH_BY_TEL: 
					yield address.getTel();
				
				default: yield address.getName();
			};
			
			//선택된 문자열 캐릭터형 리스트 컬렉션으로 변경
			List<Character> valueToChar=CommonUtils.getConsonants(value);
			//검색 데이터에 마지막 글자에 받침이 없을 경우 마지막 데이터를 삭제시켜서 검색
			//ex.'가' 검색시 가길동,강길동 등을 포함/ '가ㄱ' 검색시 가길동,가기동 등 포함
			if (searchToChar.get(searchToChar.size()-1)==' ') searchToChar.remove(searchToChar.size()-1);
			
			
			if(isName) { //이름 검색
				for(int i=0; i<searchToChar.size(); i++) {
					//하나라도 틀리면 break
					if(searchToChar.get(i).charValue()!=valueToChar.get(i).charValue())break;
					
					//전부 일치하면 검색된 리스트에 객체 추가
					if(i==searchToChar.size()-1) searchAddressList.add(address);
				}
			}else { //주소, 전화번호 검색
				String searchText=String.join("", searchToChar.stream().map(String::valueOf).collect(Collectors.toList()));;
				String compareText=String.join("", valueToChar.stream().map(String::valueOf).collect(Collectors.toList()));;
				if(compareText.contains(searchText)) searchAddressList.add(address);
			}
			
		}
		return searchAddressList;
	}
	
	//저장된 데이터 불러오기
	public void loadAddress() {
		ObjectInputStream ois=null;
		FileInputStream fis=null;
		//BufferedReader br=null;
		//FileReader fr=null;
		try {
			if(addressDataFile.exists()) {
				fis=new FileInputStream(addressDataFile);
				ois=new ObjectInputStream(fis);
				//파일 -> 리스트
				addressList=(List<Address>) ois.readObject();
				/*
				fr=new FileReader(new File("data/address.csv"));
				br=new BufferedReader(fr);
				String line;
				while((line=br.readLine())!=null) {
					String[] row=line.split(",");
					addressList.add(new Address(row[0],Integer.parseInt(row[1]),row[2],row[3].replaceAll("-", "")));
				}
				*/
				//리스트가 존재할 경우
				if(addressList!=null) {
					//테이블에 데이터 세팅 및 정렬
					sortTable(addressList,selectedColumn,orderType);
					
					//최근 수정된 날짜
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd a HH:mm");
					String dateStr=sdf.format(new Date(addressDataFile.lastModified()));
					frame.setTitle(frame.getTitle()+"("+dateStr+" 기준)");
				}
			}
		}catch(ClassNotFoundException exception) {
			exception.printStackTrace();
		}catch(IOException exception){
			exception.printStackTrace();
		}finally {
			try {
				if(ois!=null)ois.close();
				if(fis!=null)fis.close();
				//if(fr!=null)fr.close();
				//if(br!=null)br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//테이블에 데이터 수
	private void editDataSizeLabel(List<Address> list) {
		dataSizeLabel.setText("데이터 수:"+list.size());
	}
	
	//예,아니오 확인 다이얼로그
	private int getConfirmNumber(String type) {
		return JOptionPane.showConfirmDialog(frame, 
											type+" 하시겠습니까?", 
											type+"확인",
											JOptionPane.YES_NO_OPTION);
	}//getConfirmNumber
	
	
	//테이블 데이터 세팅 및 정렬
	private void sortTable(List<Address> addressList, int sortType, int orderType) {
		//선택된 컬럼이 없을 경우 이름으로 정렬
		if(sortType==-1) sortType=Address.SORT_BY_NAME;
		// 정렬 방식 세팅
		Address.setSortType(sortType);
		// 오름차순,내림차순 세팅
		Address.setOrderType(orderType);
		// 컬렉션 리스트 정렬
		Collections.sort(addressList);
		//tableModel 초기화
		tableModel.setRowCount(0);
		//tableModel 데이터 세팅
		for(Address address:addressList) {
			Object[] row= {address.getName(), 
					String.valueOf(address.getAge()), 
					address.getAddr(), 
					address.getTel()};
			tableModel.addRow(row);
		}
		//데이터 수 세팅
		editDataSizeLabel(addressList);
	}
	

	public void setTable(JTable table) {
		this.table = table;
	}


	public void setSortComboBox(JComboBox<String> sortComboBox) {
		this.sortComboBox = sortComboBox;
	}


	public void setSearchTextField(JTextField searchTextField) {
		this.searchTextField = searchTextField;
	}


	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}


	public void setExportButton(JButton exportButton) {
		this.exportButton = exportButton;
	}


	public void setDeleteButton(JButton deleteButton) {
		this.deleteButton = deleteButton;
	}

	public AddressBookLayout getFrame() {
		return frame;
	}

	public void setFrame(AddressBookLayout frame) {
		this.frame = frame;
	}


	public void setNameField(JTextField nameField) {
		this.nameField = nameField;
	}


	public void setAgeField(JTextField ageField) {
		this.ageField = ageField;
	}


	public void setAddrField(JTextField addrField) {
		this.addrField = addrField;
	}


	public void setTelField(JTextField telField) {
		this.telField = telField;
	}


	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}


	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}


	public void setTableModel(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	public void setSearchComBox(JComboBox<String> searchComboBox) {
		this.searchComboBox=searchComboBox;
	}

	public void setDataSizeLabel(JLabel dataSizeLabel) {
		this.dataSizeLabel=dataSizeLabel;
		
	}

	
}