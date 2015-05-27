package huawei.texaspoker;

import java.util.ArrayList;
import java.util.List;


public class preFlopAction {
	private int[][] MPSeatHoleCards={
		    {9,6,3,3,0,0,0,0,0,0,0,0,0},
			{6,9,1,0,0,0,0,0,0,0,0,0,0},
			{3,1,9,0,0,0,0,0,0,0,0,0,0},
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
			{3,1,9,1,1,1,0,0,0,0,0,0,0},
			{3,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,0,0,0,0,3,1,1,0,0,0,0,0},
			{0,0,0,0,0,0,3,1,1,0,0,0,0},
			{0,0,0,0,0,0,0,1,1,1,0,0,0},
			{0,0,0,0,0,0,0,0,1,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1},   
			
	};//cutOff(button前位置)
	private int[][] buttonSeatHoleCards={
			{9,6,3,1,1,1,1,1,1,1,1,1,1},
			{6,9,3,1,1,1,1,0,0,0,0,0,0},
			{3,3,9,1,1,1,1,0,0,0,0,0,0},
			{1,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,0,0,0,0,3,1,1,0,0,0,0,0},
			{1,0,0,0,0,0,3,1,1,0,0,0,0},
			{1,0,0,0,0,0,0,1,1,1,0,0,0},
			{0,0,0,0,0,0,0,0,1,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1},
			
	};//button起手牌谱
	private int[][] twoPlayersHoleCards={
			{9,6,6,1,1,1,1,1,1,1,1,1,1},
			{6,9,3,1,1,1,1,1,1,0,0,0,0},
			{6,3,9,1,1,1,1,0,0,0,0,0,0},
			{1,1,1,6,1,1,1,0,0,0,0,0,0},
			{1,1,1,1,6,1,1,0,0,0,0,0,0},
			{1,1,1,1,1,3,1,1,0,0,0,0,0},
			{1,1,1,0,0,0,3,1,1,0,0,0,0},
			{1,1,0,0,0,0,0,1,1,1,0,0,0},
			{1,0,0,0,0,0,0,0,1,1,1,0,0},
			{1,0,0,0,0,0,0,0,0,1,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,0},
			{0,0,0,0,0,0,0,0,0,0,0,0,1},
			
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
      if(playerJoinIn>=5){
    	  return moreThan4Plyers();
      }else{
    	  if(playerJoinIn>2){
    		  return moreThan2Players();
    	  }else{
    		  return twoPlayers();
    	  }
      }
	 }
	public String moreThan4Plyers(){
		int min = Math.min(holeCards.get(0).number, holeCards.get(1).number);
		int max = Math.max(holeCards.get(0).number, holeCards.get(1).number);
		int preFlopRank = 0;
		if (currentSeat == 8 ) {
			if (holeCards.get(0).suit == holeCards.get(1).suit) {
				preFlopRank = buttonSeatHoleCards[14 - min][14 - max];
			} else {
				preFlopRank = buttonSeatHoleCards[14 - min][14 - max];
			}
		} else {
			if (currentSeat == 7) {
				if (holeCards.get(0).suit == holeCards.get(1).suit) {
					preFlopRank = cutOffSeatHoleCards[14 - min][14 - max];
				} else {
					preFlopRank = cutOffSeatHoleCards[14 - min][14 - max];
				}
			} else {
				if (holeCards.get(0).suit == holeCards.get(1).suit) {
					preFlopRank = MPSeatHoleCards[14 - min][14 - max];
				} else {
					preFlopRank = MPSeatHoleCards[14 - min][14 - max];
				}
			}
		}
		switch (preFlopRank) {
		case 9:
			return "raise " + Math.min(potSize * 1 / 2, myRestJetton);
		case 6:
			if(timeOfBet>1&&bet>BB&&myRestJetton>=5*BB){
				return "fold";
			}else{
				if (bet <=BB&&timeOfBet==1) {
					return "raise "
							+ Math.min( potSize * 1 / 2, myRestJetton);
				} else {
					if(bet<=2*BB)
					return "call";// 其他位置call
					else
					return "fold";
				}
			}
			
		case 3:
			if(timeOfBet>1&&bet>1/5*potSize&&myRestJetton>=5*BB){
				return "fold";
			}else{
				if (bet <=BB&&timeOfBet==1) {
					return "raise "
							+ Math.min( potSize * 1 / 2, myRestJetton);
				} else {
						if(bet<2*BB)
						return "call";// 其他位置call
						else
						return "fold";
				}
			}
		case 1:
			if (currentSeat == 2) {
				if (myRestJetton < 4 * BB) {
					return "all_in";
				} else {
					if (bet >0) {
						return "fold";
					} else {
						return "check";// 大盲位check
					}
				}
			} else {
				if (myRestJetton < 4 * BB) {
					return "all_in";
				} else {
						return "fold";// 其他位置call				
				}
			}
		case 0:
			if (currentSeat == 2) {
				if (myRestJetton <= 2 * BB) {
					return "all_in";
				} else {
					if (bet > 0) {
						return "fold";
					} else {
						return "check";// 大盲位check
					}
				}
			} else {
				return "fold";// 其他位置call
			}
		default:
			return "check";
		}
	}
    public String moreThan2Players(){
    	int min=Math.min(holeCards.get(0).number, holeCards.get(1).number);
        int max=Math.max(holeCards.get(0).number, holeCards.get(1).number);
        int preFlopRank=0;
        if(currentSeat==8||currentSeat==1){
      	  if(holeCards.get(0).suit==holeCards.get(1).suit){
      		  preFlopRank=buttonSeatHoleCards[14-min][14-max];	
      	  }else{
      		  preFlopRank=buttonSeatHoleCards[14-min][14-max];
      	  }
  	  }else{
  		  if(currentSeat==7){
  			  if(holeCards.get(0).suit==holeCards.get(1).suit){
  	    		  preFlopRank=cutOffSeatHoleCards[14-min][14-max];	
  	    	  }else{
  	    		  preFlopRank=cutOffSeatHoleCards[14-min][14-max];
  	    	  }  
  		  }else{
  			  if(holeCards.get(0).suit==holeCards.get(1).suit){
  	    		  preFlopRank=MPSeatHoleCards[14-min][14-max];	
  	    	  }else{
  	    		  preFlopRank=MPSeatHoleCards[14-min][14-max];
  	    	  }
  		  }
  	  }
  		switch (preFlopRank) {
  		case 9:
  			return "raise "+Math.min(2*BB+potSize*1/2, myRestJetton);
  		case 6:
  			if(bet<=2*BB&&timeOfBet==1){
  				return "raise "+Math.min(potSize*1/2, myRestJetton);
  			}else{  				
					if (currentSeat == 2) {
						return "check";// 大盲位check
					} else {
						return "fold";
					}	
  			}
  		case 3:
  			if(timeOfBet>1&&bet>2*BB&&myRestJetton>=5*BB){
				return "fold";
			}else{
				if (bet < 2 * BB&&timeOfBet==1) {
					return "raise "
							+ Math.min( potSize * 1 / 2, myRestJetton);
				} else {
					if (currentSeat == 2) {
						return "check";// 大盲位check
					} else {						
						return "fold";// 其他位置call
					}
				}
			}
  		case 1:
  			if(bet<=BB&&timeOfBet==1){
  				
  				return  "raise "+Math.min(potSize*1/2, myRestJetton);
  			}else{
  				
  				if(currentSeat==2){
  					if(myRestJetton<4*BB){
  						return "all_in";
  					}else{
  						if(bet>0){
  							return "fold";
  						}else{
  							return "check";//大盲位check
  						}
  					}				
  				}else{
  					if(myRestJetton<4*BB){
  						return "all_in";
  					}else{
  						return "fold";
  					}				
  				}
  			}
  		case 0:
  			if(currentSeat==2){
  				if(myRestJetton<=2*BB){
  					return "all_in";
  				}else{
  					return "check";
  				}		
  			}else{
  				return "fold";//其他位置call
  			}	
  		default:
  			return "fold";
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
  			if(bet<=2*BB&&timeOfBet==1){
  				return "raise "+Math.min(2*BB+potSize*1/2, myRestJetton);
  			}else{
  				if(timeOfBet>1){
  					return "fold";
  				}else{
  					if(currentSeat==1){
  	  					return "check";//大盲位check
  	  				}else{
  	  					return "call";//其他位置call
  	  				}	
  				}
  					
  			}
  		case 3:
  			if(bet>2*BB&&timeOfBet>1){
  				if(bet>=2*BB&&myRestJetton>4*BB){
  					return "fold";
  				}else
  				 return "call";
  			}else{
  				if(timeOfBet==1)
  				return 	"raise "+Math.min(potSize*1/2, myRestJetton);
  				else
  				return "fold";
  			}
  		case 1:
  			if(bet<=BB&&timeOfBet==1){
  				return  "raise "+Math.min(Math.min(3*BB,2*BB+potSize*1/2), myRestJetton);
  			}else{
  				if(currentSeat==1){
  					if(myRestJetton<4*BB){
  						return "all_in";
  					}else{
  						return "check";
  					}				
  				}else{
  					return "fold";			
  				}
  			}
  		case 0:
  			if(currentSeat==1){
  				if(myRestJetton<=2*BB){
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
  			return "check";
  		}
	}
}
