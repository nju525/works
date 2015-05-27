package huawei.texaspoker;

import java.util.List;
import java.util.Map;


public class pokerStrategy {

/**
 * 决策后反馈的动作
 */
	private int Betdecision_Raise=0;
	private int Betdecision_Call=1;
	private int Betdecision_Fold=2;
    
	private int[][] betterSeatHoleCards={};//位置好时的起手牌力牌普
	private int[][] worseSeatHoleCards={};//位置不太好时的起手牌力牌普
	
	private int currentHand;//当前手牌数
	
	private Map<Integer,historyPlayer> historyMap;//历史动作信息 接收到 【询问消息】 【摊牌消息】的时候进行添加
	
    private List<Card> holeCards;//每次取得的手牌（自己的两张牌）  接收到  【手牌信息】  的时候执行 添加
    private List<Card> sharedCards;//公共牌  接收到 【公牌信息】【转牌信息】【河牌信息】 的时候进行添加 
    
    private int currentSeat;//当前位置信息 第一手牌进行判断 以后加1mod(8);1-8 1代表位置最好

    

/**
 *  翻拍前根据手牌强度进行决策判断 
 */
	public int preFlopDesicion(){
	// TODO Auto-generated method stub
		if(currentSeat<=2) return betterSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];
		else return worseSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];
	}
/**
 *  翻拍后根据手牌强度进行决策判断 
 */
	public int afterFlop(){
	// TODO Auto-generated method stub
		
		return 0;
	}
	
}
