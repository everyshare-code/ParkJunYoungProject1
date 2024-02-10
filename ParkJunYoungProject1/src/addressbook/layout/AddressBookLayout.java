package addressbook.layout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import addressbook.controller.AddressBookHandler;

public class AddressBookLayout extends JFrame{
	private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> sortComboBox;
    private JComboBox<String> searchComboBox;
    private JTextField searchTextField;
    private JButton addButton;
    private JButton exportButton;
    private JButton deleteButton;
    private JLabel dataSizeLabel;
    
    private AddressBookHandler handler;
   
    private String[] searchSelectArr= {"이름으로 검색","주소로 검색","전화번호로 검색"};
    private String[] colNames= {"이름","나이","주소","전화번호"};
    
	private Dimension deviceSize;
	
	
	
	public AddressBookLayout() {
		
		//프로그램 제목
		setTitle("주소록 프로그램");
		//현재 기기 사이즈
		deviceSize=Toolkit.getDefaultToolkit().getScreenSize();
		//시작 x좌표
		int startX=(int)(deviceSize.width*0.2);
		//시작 y좌표
		int startY=(int)(deviceSize.height*0.2);
		//컴포넌트 가로 크기
		int width=(int)(deviceSize.width*0.6);
		//컴포넌트 세로 크기
		int height=(int)(deviceSize.height*0.6);
		//좌표, 크기 적용
		setBounds(startX,startY,width,height);
		
		//프로그램 종료 막기
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// 메인 패널 생성
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 테이블 모델 생성
        tableModel = new DefaultTableModel(colNames, 0) {
        	//테이블 내용 수정 불가(더블 클릭으로 입력 상태 막기)
        	@Override
        	public boolean isCellEditable(int row, int column) {
        		return false;
        	}
        };
        //테이블 생성, 테이블 모델 설정
        table = new JTable(tableModel);
        //테이블 헤더 폰트 설정
        Font headerFont=new Font(table.getTableHeader().getFont().getName(),Font.BOLD,12);
        table.getTableHeader().setFont(headerFont);
        //테이블 헤더 문자열 가운데 정렬
        DefaultTableCellRenderer headerRenderer=
        		(DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        //테이블 컬럼별 사이즈 조절
        TableColumnModel columnModel=table.getColumnModel();
        //현재 테이블 사이즈
        double tableWidth=table.getPreferredSize().getWidth();
        //이름 컬럼 사이즈
        int nameColumnSize=(int)(tableWidth*0.05);
        //나이 컬럼 사이즈
        int ageColumnSize=(int)(tableWidth*0.05);
        //주소 컬럼 사이즈
        int addrColumnSize=(int)(tableWidth*0.7);
        //전화번호 컬럼 사이즈
        int telColumnSize=(int)(tableWidth*0.2);
        //컬럼별 사이즈 지정
        columnModel.getColumn(0).setPreferredWidth(nameColumnSize);
        columnModel.getColumn(1).setPreferredWidth(ageColumnSize);
        columnModel.getColumn(2).setPreferredWidth(addrColumnSize);
        columnModel.getColumn(3).setPreferredWidth(telColumnSize);
        
        
        table.getTableHeader().setReorderingAllowed(false); //이동 불가
        table.getTableHeader().setResizingAllowed(false);//크기 조절 불가
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //행 하나만 선택 가
        JScrollPane tableScrollPane = new JScrollPane(table);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 하단 패널 생성
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        // 검색 방식 선택 콤보 박스
        searchComboBox = new JComboBox<>(searchSelectArr);
        controlPanel.add(searchComboBox);

        // 검색 텍스트 필드
        searchTextField = new JTextField(20);
        controlPanel.add(searchTextField);

        // 버튼추가
        addButton = new JButton("주소록 추가");
        controlPanel.add(addButton);

        //삭제 버튼
        deleteButton = new JButton("삭제");
        controlPanel.add(deleteButton);
        
        exportButton = new JButton("내보내기");
        controlPanel.add(exportButton);
        
        //정렬 라벨
        dataSizeLabel=new JLabel("주소록 수:");
        controlPanel.add(dataSizeLabel);
        
        //하단 패널 메인 패널에 추가
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        //메인 패널 프레임에 추가
        add(mainPanel);
        
        
       
		//핸들러 생성, 이벤트 컴포넌트 리스트 전달
		handler=new AddressBookHandler(this);
		setHandler();
		//이벤트 핸들러 설정
		addButton.addActionListener(handler);
		exportButton.addActionListener(handler);
		deleteButton.addActionListener(handler);
		searchTextField.addKeyListener(handler);
		//sortComboBox.addActionListener(handler);
		searchComboBox.addActionListener(handler);
		table.getTableHeader().addMouseListener(handler);
		table.addMouseListener(handler);
		addWindowListener(handler);
		
		//이벤트 핸들러에서 파일로 저장된 데이터 불러오기
		handler.loadAddress();
		
        setVisible(true);
    
	}
	
	public void setHandler() {
		handler.setAddButton(addButton);
		handler.setExportButton(exportButton);
		handler.setDeleteButton(deleteButton);
		handler.setSearchTextField(searchTextField);
		handler.setSortComboBox(sortComboBox);
		handler.setSearchComBox(searchComboBox);
		handler.setTable(table);
		handler.setTableModel(tableModel);
		handler.setDataSizeLabel(dataSizeLabel);
	}
	
}
