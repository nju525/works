package huawei.texaspoker;
import java.util.HashMap;
import java.util.List;

/**
 * 对手类
 * @author SQQ
 *
 */
public class Opponent {
	private int pid;//对手id
	private int jetton;//筹码数
	private int money;//金币数
	public int bet_in=0;//本局该玩家已投入的筹码数
	public int order;//玩家执行次序
	public boolean isDiscard;
	public HashMap<Integer, List<String>> action;//第x轮的动作, check | call | raise | all_in | fold
	public Opponent(int id,int jetton,int money,int order){
		this.pid=id;
		this.jetton=jetton;
		this.money=money;
		this.order=order;
		isDiscard=false;
		action=new HashMap<Integer, List<String>>();		
	}
	public int getPID(){
		return pid;
	}
	public int getJetton(){
		return jetton;
	}
	public int getMoney(){
		return money;
	}
	public void setPID(int pid){
		this.pid=pid;
	}
	public void setJetton(int jetton){
		this.jetton=jetton;
	}
	public void setMoney(int money){
		this.money=money;
	}
}
