

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Desk记录牌局信息：大小盲、庄家的pid
 * @author SQQ
 *
 */
public class Desk {
	private int smallblind;
	private int bigblind;
	private int button;
	private int BB;//（大）盲注金额
	private int cardStatus;//牌局状态=hold(0)、flop(1)、turn(2)、river(3)……
	public List<Card> sharedCards;//存储公共牌
	public int totalpot;//底池金额
	public int playercount;
	public Map<Integer, Integer> Order_Pid;//根据行动顺序order获得对手pid
	public Desk(int smallblind,int bigblind,int button){
		this.smallblind=smallblind;
		this.bigblind=bigblind;
		this.button=button;
		this.sharedCards=new ArrayList<Card>(5);
		this.Order_Pid=new HashMap<Integer, Integer>();
	}
	public int getSmallBlind(){
		return smallblind;
	}
	public int getBigBlind(){
		return bigblind;
	}
	public void setButton(int button){
		this.button=button;
	}
	public void setSmallBlind(int smallblind){
		this.smallblind=smallblind;
	}
	public void setBigBlind(int bigblind){
		this.bigblind=bigblind;
	}
	public int getButton(){
		return button;
	}
	public void setBB(int BB){
		this.BB=BB;
	}
	public int getBB(){
		return BB;
	}
	public void setcardStatus(int cardStatus){
		this.cardStatus=cardStatus;
	}
	public int getcardStatus(){
		return cardStatus;
	}
}
