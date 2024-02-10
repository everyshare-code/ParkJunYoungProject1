package addressbook.model;

import java.io.Serializable;
import java.util.List;

public class Address implements Serializable,Comparable<Address>{
	public static final int SORT_BY_NAME=1; //이름으로 정렬
	public static final int SORT_BY_AGE=2; //나이로 정렬
	public static final int SORT_BY_ADDR=3; //주소로 정렬
	public static final int SORT_BY_TEL=4; //전화번호로 정렬
	public static final int ORDER_BY_ASCENDING=10; //오름차순
	public static final int ORDER_BY_DESCENDING=20; //내림차순
	private static int sortType=SORT_BY_NAME; //정렬 타입
	private static int orderType=ORDER_BY_ASCENDING; //정렬 순서
	private String name;
	private int age;
	private String addr;
	private String tel;
	//인자 생성자
	public Address(String name, int age, String addr, String tel) {
		this.name = name;
		this.age = age;
		this.addr = addr;
		this.tel = tel;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public static void setSortType(int sortType) {
		Address.sortType = sortType;
	}
	
	public static void setOrderType(int orderType) {
		Address.orderType=orderType;
	}
	
	public static int getOrderType() {
		return Address.orderType;
	}
	
	@Override
	public String toString() {
		return "[이름=" + name + ",나이=" + age + ",주소=" + addr + ",전화번호=" + tel + "]";
	}
	@Override
	public int compareTo(Address o) {
		//정렬 타입과 순서에 따라서 정렬 방식 변경
		switch(Address.sortType) {
			case SORT_BY_NAME:
				if(orderType==ORDER_BY_ASCENDING) return name.compareTo(o.getName());
				else return o.getName().compareTo(name); 
			case SORT_BY_AGE:
				if(orderType==ORDER_BY_ASCENDING) return age-o.getAge();
				else return o.getAge()-age; 
			case SORT_BY_ADDR: 
				if(orderType==ORDER_BY_ASCENDING) return addr.compareTo(o.getAddr());
				else return o.getAddr().compareTo(addr); 
			case SORT_BY_TEL: 
				if(orderType==ORDER_BY_ASCENDING) return tel.compareTo(o.getTel());
				else return o.getTel().compareTo(tel); 
			default: return 0;
		}
	}
	
}
