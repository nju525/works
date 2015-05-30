
import java.util.HashMap;
import java.util.Map;

public class Card {
	public int suit;//花色  0-SPADES 1-HEARTS 2-CLUBS 3-DIAMONDS
	public  int number;//数字 1-13 ||A=14 J=11 Q=12 K=13
	public Card(int suit,int number){
		this.suit=suit;
		this.number=number;
	}
	public Card(){
		
	}
	public int getSuit(){
		return suit;
	}
	public int getNumber(){
		return number;
	}
	public final static Map<String, Integer> NumeralSuit=new HashMap<String, Integer>();
	
	static {//静态的变量用静态代码块来初始化
		NumeralSuit.put("SPADES", 0);
		NumeralSuit.put("HEARTS", 1);
		NumeralSuit.put("CLUBS", 2);
		NumeralSuit.put("DIAMONDS",3);
	}
}
