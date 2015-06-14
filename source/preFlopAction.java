

import java.util.ArrayList;
import java.util.List;

//5月31日修改
/**
 * 	1.增加有利位置的入池率 多进行抢盲动作
 *  2.加注尺度进行调整  强度为1 的加注2.5BB 
 * @author Administrator
 *
 */
public class preFlopAction {
	private int[][] MPSeatHoleCards={
		    {9,6,3,3,0,0,0,0,0,0,0,0,0},
			{6,9,1,0,0,0,0,0,0,0,0,0,0},
			{3,1,6,0,0,0,0,0,0,0,0,0,0},
			{3,0,0,6,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,6,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,3,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,3,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,1,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,1,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1},
			
	};//靠前位置
	private int[][] cutOffSeatHoleCards={
			{9,6,3,3,1,1,1,1,1,1,1,0,0},
			{6,9,1,1,1,1,1,0,0,0,0,0,0},
			{3,1,6,1,1,1,0,0,0,0,0,0,0},
			{3,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,0,0,0,0,3,1,1,0,0,0,0,0},
			{1,0,0,0,0,0,3,1,0,0,0,0,0},
			{0,0,0,0,0,0,0,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,0,1,1,0,0,0},
			{0,0,0,0,0,0,0,0,0,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1},   
			
	};//cutOff(button前位置)
	private int[][] buttonSeatHoleCards={
			{9,6,3,3,1,1,1,1,1,1,1,0,0},
			{6,9,3,1,1,1,1,1,1,0,0,0,0},
			{3,3,6,1,1,1,1,1,0,0,0,0,0},
			{3,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,1,1,1,1,3,1,1,0,0,0,0,0},
			{1,1,1,0,0,1,3,1,1,0,0,0,0},
			{1,1,1,0,0,0,1,1,1,1,0,0,0},
			{1,1,0,0,0,0,0,0,1,1,1,0,0},
			{1,1,0,0,0,0,0,0,1,1,1,0,0},
			{1,0,0,0,0,0,0,0,0,0,1,0,0},
			{1,0,0,0,0,0,0,0,0,0,0,1,0},
			{1,0,0,0,0,0,0,0,0,0,0,0,1},
			
	};//button起手牌谱
	private int[][] twoPlayersHoleCards={
			{9,6,6,3,1,1,1,1,1,1,1,0,0},
			{6,9,3,1,1,1,1,1,1,0,0,0,0},
			{6,3,6,1,1,1,1,0,0,0,0,0,0},
			{3,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,1,1,1,1,3,1,1,0,0,0,0,0},
			{1,1,1,1,1,1,3,1,1,0,0,0,0},
			{1,1,0,0,0,0,1,1,1,1,0,0,0},
			{1,0,0,0,0,0,0,0,1,1,1,0,0},
			{1,0,0,0,0,0,0,0,0,1,1,0,0},
			{1,0,0,0,0,0,0,0,0,0,1,0,0},
			{1,0,0,0,0,0,0,0,0,0,0,1,0},
			{1,0,0,0,0,0,0,0,0,0,0,0,1},
			
	};//2人单挑
	
	private List<Card> holeCards=new ArrayList<Card>();
	private int currentSeat;
	private int bet;
	private int BB;
	private int potSize;
	private int myRestJetton;
	private int playerJoinIn;
	private int timeOfBet;
	public preFlopAction(List<Card> holeCards,int currentSeat,int bet,int BB,int potSize,int playerJoinIn,int myRestJetton, int timeOfBet){
		this.holeCards=holeCards;
		this.currentSeat=currentSeat;
		this.bet=bet;
		this.BB=BB;
		this.potSize=potSize;
		this.playerJoinIn=playerJoinIn;
		this.myRestJetton=myRestJetton;
		this.timeOfBet=timeOfBet;
	}
	public String preFlopDecision(){
		// TODO Auto-generated method stub
			/*if(currentSeat<=2) return betterSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];
			else return worseSeatHoleCards[holeCards.get(0).number][holeCards.get(1).number];*/
		
	/**
      *1. 牌力强度为9的 会一直raise到 allin
      *2. 为6的会 在前面都平跟或小raise时 进行再raise
      *3. 为3的 前面raise时 平跟 前面平跟时raise
      *4. 为1的 前面无人动作时 raise 有人动作时 fold
      */
      if(playerJoinIn>2){
    	  return moreThan4Plyers();
      }else{    	  
    		  return twoPlayers();
      }
	 }
	public String moreThan4Plyers(){
		int min = Math.min(holeCards.get(0).number, holeCards.get(1).number);
		int max = Math.max(holeCards.get(0).number, holeCards.get(1).number);
		int preFlopRank = 0;
		if (currentSeat == 8||currentSeat==1||currentSeat==2) {
			if (holeCards.get(0).suit == holeCards.get(1).suit) {
				preFlopRank = buttonSeatHoleCards[14 - min][14 - max];
			} else {
				preFlopRank = buttonSeatHoleCards[14 - max][14 - min];
			}
		} else {
			if (currentSeat == 7) {
				if (holeCards.get(0).suit == holeCards.get(1).suit) {
					preFlopRank = cutOffSeatHoleCards[14 - min][14 - max];
				} else {
					preFlopRank = cutOffSeatHoleCards[14 - max][14 - min];
				}
			} else {
				if (holeCards.get(0).suit == holeCards.get(1).suit) {
					preFlopRank = MPSeatHoleCards[14 - min][14 - max];
				} else {
					preFlopRank = MPSeatHoleCards[14 - max][14 - min];
				}
			}
		}
		switch (preFlopRank) {
		case 9:			
				return "raise " + Math.min(2*BB+potSize, myRestJetton);			
		case 6:
			//牌力为6时 前面bet为大盲进行3BB 加注 前面进行加注时 进行跟注 别人反加时 若反加大小等于当前投入量 进行跟注
			if(bet<=BB){
				if(currentSeat==2){
				    if(bet==BB)return "call";
				    else
					return "raise " + Math.min(BB+potSize*1/2, myRestJetton);
				}else{
					return "raise " + Math.min(BB+potSize, myRestJetton);	 
				}
			}else{
				if(bet<=4*BB)
				return "call";
			}			
		case 3:
			//牌力为3时 前面只有少于一个人跟注时进行加注 有人加注时 非大盲位弃牌   有反加时 超过3*BB 弃牌 
			if(potSize<=2.5*BB){
				return "raise " + Math.min(BB+potSize*1/2, myRestJetton);
			}else{
				if(currentSeat==2){
					if(bet<=2*BB) return "call";
					else
					return "fold";
				}else{
					if(bet<=2*BB){
						return "call";
					}else{
						return "fold";
					}
				}
			}
		case 1:
			//牌力为1时 若在前面有人call或加注时 进行弃牌 若无人行动时 加注1.5BB
			if(potSize<=2*BB){
				return "raise " +1.5*BB;
			}else{
				if(currentSeat==2){
					if(bet==0){
						return "check";
					}else{
						return "fold";
					}
				}else{
					if(currentSeat==1&&bet==20){
						return "call";						
					}else
					return "fold";
				}
			}
		case 0:
			if (currentSeat == 2) {
				if (myRestJetton <= 3 * BB) {
					return "all_in";
				} else {
					if (bet > 0) {
						return "fold";
					} else {
						return "check";// 大盲位check
					}
				}
			} else {
			return "fold";
			}
		default:
			return "check";
		}
	}
   
    public String twoPlayers(){
    	int min=Math.min(holeCards.get(0).number, holeCards.get(1).number);
        int max=Math.max(holeCards.get(0).number, holeCards.get(1).number);
        int preFlopRank=0;
        preFlopRank=twoPlayersHoleCards[14-min][14-max];
  		switch (preFlopRank) {
  		case 9:
  			return "raise "+Math.min(2*BB+potSize*1/2, myRestJetton);
  		case 6:
  			if(bet<=BB){
				if(currentSeat==2){
				    if(bet==BB)return "call";
				    else
					return "raise " + Math.min(BB+potSize*1/2, myRestJetton);
				}else{
					return "raise " + Math.min(BB+potSize, myRestJetton);	 
				}
			}else{
				if(bet<=4*BB)
				return "call";
			}	
  		case 3:
  			if(potSize<=2*BB){
				return "raise " + Math.min(2*BB+potSize*1/2, myRestJetton);
			}else{
				if(currentSeat==1){
					if(bet<=4*BB) return "call";
					else
					return "fold";
				}else{
					if(bet==BB){
						return "call";
					}else{
						return "fold";
					}
				}
			}
  		case 1:
  			if(potSize<=BB){
				return "raise " +2*BB;
			}else{
				if(currentSeat==1){
					if(bet==0){
						return "check";
					}else{
						return "fold";
					}
				}else{
					if(bet==BB){
						return "call";
					}else{
						return "fold";
					}
				}
			}
  		case 0:
  			if(currentSeat==1){
  				if(myRestJetton<=3*BB){
  					return "all_in";
  				}else{
  					if(bet>BB){
  						return "fold";
  					}else{
  						return "check";//大盲位check
  					}
  				}		
  			}else{ 				
  				return "fold";//其他位置call
  			}	
  		default:
  			return "fold";
  		}
	}
}
