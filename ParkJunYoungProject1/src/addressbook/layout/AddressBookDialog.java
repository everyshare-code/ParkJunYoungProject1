package addressbook.layout;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import addressbook.controller.AddressBookHandler;
import addressbook.model.Address;

public class AddressBookDialog extends JDialog{
	private JTextField nameField;
    private JTextField ageField;
    private JTextField addrField;
    private JTextField telField;
    private AddressBookHandler handler;
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean isEdit;
    
    public AddressBookDialog(AddressBookLayout owner,String type,Address address,AddressBookHandler handler) {
    	// 모달 다이얼로그
    	super(owner, type, true); 
        this.handler=handler;
        
        if(type.equals("수정")) isEdit=true;
        
        //텍스트 필드 컴포넌트 생성
        nameField = new JTextField(20);
        ageField = new JTextField(5);
        addrField = new JTextField(30);
        telField = new JTextField(15);
        //전달받은 address 객체 null이 아닐 경우 텍스트 필드 값 채워주기(수정 작업을 위해 추가)
        if(address!=null) {
        	nameField.setText(address.getName());
        	ageField.setText(String.valueOf(address.getAge()));
        	addrField.setText(address.getAddr());
        	telField.setText(address.getTel());
        }
        nameField.setName("이름");
        ageField.setName("나이");
        addrField.setName("주소");
        telField.setName("전화번호");
        
        
        //버튼 컴포넌트 생성
        saveButton = new JButton("저장");
        cancelButton = new JButton("취소");
        
        //라벨 컴포넌트 생성
        JLabel nameLabel=new JLabel("이름:");
        JLabel addrLabel=new JLabel("주소:");
        JLabel ageLabel=new JLabel("나이:");
        JLabel telLabel=new JLabel("전화번호:");
        //라벨 가로 위치 가운데로 설정
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        ageLabel.setHorizontalAlignment(JLabel.CENTER);
        addrLabel.setHorizontalAlignment(JLabel.CENTER);
        telLabel.setHorizontalAlignment(JLabel.CENTER);

        // 생성된 컴포넌트를 담을 패널 생성
        JPanel panel = new JPanel(new GridLayout(5, 2));
        
        //패널에 컴포넌트 담기
        //1행
        panel.add(nameLabel);
        panel.add(nameField);
        //2행
        panel.add(ageLabel);
        panel.add(ageField);
        //3행
        panel.add(addrLabel);
        panel.add(addrField);
        //4행
        panel.add(telLabel);
        panel.add(telField);
        //5행
        panel.add(saveButton);
        panel.add(cancelButton);
        
        //핸들러에 이벤트 발생시킬 객체 넘겨주기
        setHandler();
        
        //이벤트 발생 시킬 객체에 리스너 등록
        nameField.addKeyListener(handler);
        ageField.addKeyListener(handler);
        addrField.addKeyListener(handler);
        telField.addKeyListener(handler);
        saveButton.addActionListener(handler);
        cancelButton.addActionListener(handler);
        //윈도우 리스너 등록
        addWindowListener(handler);
        addKeyListener(handler);
        
        //사이즈 조절 불가
        setResizable(false);
        //다이얼로그에 패널 추가
        getContentPane().add(panel);
        //창 사이즈 맞춤
        pack();
        //owner 객체 가운데 위치
        setLocationRelativeTo(owner);
    }//생성자
    
    //핸들러에 이벤트 발생시킬 요소 전달
    private void setHandler(){
    	handler.setSaveButton(saveButton);
    	handler.setCancelButton(cancelButton);
    	handler.setNameField(nameField);
    	handler.setAgeField(ageField);
    	handler.setAddrField(addrField);
    	handler.setTelField(telField);
    }//setHandler
    
    //수정인지 추가인지 확인
	public boolean isEdit() {
		return isEdit;
	}//isEdit

	
    
    
    
}
