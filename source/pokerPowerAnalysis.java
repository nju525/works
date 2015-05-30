

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析当前手牌与公共牌的组合的牌力 返回牌力强弱值0-n 0表示最弱
 */
public class pokerPowerAnalysis {
	private List<Card> holeCards=new ArrayList<Card>();
	private List<Card> sharedCards=new ArrayList<Card>();
	private List<Card> allCards=new ArrayList<Card>(); // 全部牌
	//当前最大牌型组合
	public int[] currentMaxHand=new int[5];
	public int[] longestStraight=new int[4];

	/**
	 * 构造函数 传入手牌和公共牌
	 * 
	 * @param holeCards
	 *            手牌
	 * @param sharedCards
	 *            公共牌
	 */
	public pokerPowerAnalysis(List<Card> holeCards, List<Card> sharedCards) {
		this.holeCards = holeCards;
		this.sharedCards = sharedCards;
		/*allCards.addAll(this.holeCards);
		allCards.addAll(this.sharedCards);*/
	}

	/**
	 * 具体执行手牌公共牌组合的牌力分析 返回最强牌力值
	 * 
	 * @return
	 */
	public int pokerPowerRankValue() {
		allCards.addAll(holeCards);
		allCards.addAll(sharedCards);
		Map<Integer, Integer> numberOfCards = getNumberOfCards(allCards);// 获取所有牌中的不同数字数量
		Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);// 获取所有牌中的花色数
		if(getFlushStraight(numberOfCards, suitOfCards)>0){
			return getFlushStraight(numberOfCards, suitOfCards);
		}
		if(getFourOfaKing(numberOfCards)>0){
			return getFourOfaKing(numberOfCards);
		}
		if(getFullHouse(numberOfCards)>0){
			return getFullHouse(numberOfCards);
		}
		if(getFlush(suitOfCards)>0){
			return getFlush(suitOfCards);
		}
		if(getStraight(numberOfCards)>0){
			return getStraight(numberOfCards);
		}
		if(getSet(numberOfCards)>0){
			return getSet(numberOfCards);
		}
		if(getTwoPairs(numberOfCards)>0){
			return getTwoPairs(numberOfCards);
		}
		if(getSinglePair(numberOfCards)>0){
			return getSinglePair(numberOfCards);
		}
		return getHighCard(numberOfCards);
	}

	/**
	 * 同花顺
	 * @param suitOfCards 80-89
	 * @return
	 */
	public int getFlushStraight(Map<Integer, Integer> numberOfCards,Map<Integer, Integer> suitOfCards ) {
		int colorOfFlush=-1;//记录同花的花色
		for(Integer color:suitOfCards.keySet()){
			if(suitOfCards.get(color)>=5){
				colorOfFlush=color;
				break;
			}
		}
		Map<Integer,Integer> flushMap=new HashMap<Integer,Integer>();
		if(colorOfFlush>=0){
			//int[] flushComnbs=new int[7];//存储当前最大组合的同花牌
			int index=0;
			for(Card card:allCards){
				if(card.suit==colorOfFlush){
					flushMap.put(card.number, 1); 		
				}
			}
			if(getStraight(flushMap)>0){
				return getStraight(flushMap)+40;
			}
		}		
		return 0;	
	}
	
	/**
	 * 判断是否存在炸弹  70-79
	 * @return
	 */
	public int getFourOfaKing(Map<Integer, Integer> numberOfCards) {
		int fourNumber=0;
		boolean Flag_hasFourOfaKing=false;
		int hignNumber=0;	
		for(Integer number:numberOfCards.keySet()){
			if(numberOfCards.get(number)==4){
				fourNumber=number;
				Flag_hasFourOfaKing=true;
			}else{
				if(number>hignNumber)hignNumber=number;
			}
		}
		if(Flag_hasFourOfaKing){
			int[] fourOfaKingComnbs=new int[5];
			fourOfaKingComnbs[4]=fourNumber;
			fourOfaKingComnbs[3]=fourNumber;
			fourOfaKingComnbs[2]=fourNumber;
			fourOfaKingComnbs[1]=fourNumber;
			fourOfaKingComnbs[0]=hignNumber;
			currentMaxHand=fourOfaKingComnbs;
		/**
		 * 牌力分析 
		 * 1.手牌有一张或两张 forNumber牌 【个位置为9】	 手牌无 forNumber牌  但是有顶高张 【个位置为9】
		 * 2.弱牌 【个位置为0】
		 */
			if(holeCards.get(0).number==fourNumber||holeCards.get(1).number==fourNumber||hignNumber==14||(fourNumber==14&&hignNumber==13)){
				return 79;
			}else{
				return 70;
			}
		}
		return 0;
		
	}
	/**
	 * 判断是否存在葫芦 60-69
	 * @return
	 */
	public int getFullHouse(Map<Integer, Integer> numberOfCards) {
		boolean Flag_tri=false;
		boolean Flag_dou=false;
		int triNumber=0;
		int douNumber=0;
		int highCardNumber=0;
		for(Integer number:numberOfCards.keySet()){		
			if(numberOfCards.get(number)==3){
			//	if(number==1)number=14;
				if(!Flag_tri){
					triNumber=number;
					Flag_tri=true;
				}
				else {
					if(number>triNumber){
						int tmp=triNumber;
						triNumber=number;
						if(tmp>douNumber)douNumber=tmp;//保存最大的对子和2条
					}
					Flag_dou=true;
				}
			}else{
				if(numberOfCards.get(number)==2){
				//	if(number==1)number=14;
					Flag_dou=true;
					if(number>douNumber)douNumber=number;
				}else{
					if(number>highCardNumber){
						highCardNumber=number;
					}
				}
			}
		}
		/**
		 * 存在葫芦 则存储当前最大组合葫芦牌
		 * 1.公共牌有3张  超强牌  且手上不是公共对【个位置为 9】
		 * 2.公共牌有3张   且手对是顶对 【个位置为6】  公共牌有4张 若 手牌对子大于高牌 【个位置为 6】 否则 认为次强牌 【个位置为 3】	
		 * 3.公共牌有5张 当前手牌为弱牌 置为0
		 */
		if(Flag_dou&&Flag_tri){
			int[] fullHouseComnb=new int[5];//存储当前最大牌型
			for(int i=0;i<3;i++){
				fullHouseComnb[i]=triNumber;
			}
			for(int i=3;i<5;i++){
				fullHouseComnb[i]=douNumber;
			}
			currentMaxHand=fullHouseComnb;//存储将当前最大牌型
			if(findCardsInFullHouse(sharedCards, fullHouseComnb)==3&&(holeCards.get(0).number==triNumber||holeCards.get(1).number==triNumber)){
				return 69;
			}else{				
				if((findCardsInFullHouse(sharedCards, fullHouseComnb)==3)&&holeCards.get(0).number>highCardNumber||findCardsInFullHouse(sharedCards, fullHouseComnb)==4){
					if((holeCards.get(0).number==triNumber||holeCards.get(1).number==triNumber)&&triNumber>douNumber||douNumber>highCardNumber){
						return 66;
					}else{
						return 63;
					}
				}else{
					return 60;
				}
			}
			
		}				
		return 0;
	}
	/**
	 * 判断是否存在顺子 40-49
	 * @return
	 */
	public  int getStraight(Map<Integer, Integer> numberOfCards) {
		if (numberOfCards.size() <= 4)
			return 0;
		int[] aAsNormal = new int[numberOfCards.size()];
		int index = 0;
		boolean hasAcard = false;
		for (Integer number : numberOfCards.keySet()) {
			if (number == 14)
				hasAcard = true;
			aAsNormal[index] = number;
			index++;
		}
		Arrays.sort(aAsNormal);
		int preNumber = aAsNormal[0];
		int count = 0;
		int Flag_end = 0;//有顺子的标志 存储顺子的最大位置
		for (int i = 0; i < index; i++) {
			if (aAsNormal[i] - preNumber == 1)
				count++;
			else {
				if (count >= 4) {
					Flag_end = i;
				}
				count = 0;
			}
			if (count >= 4) {
				Flag_end = i;
			}
			preNumber = aAsNormal[i];
		}
		if (Flag_end != 0) {
			int[] straightComnbs = new int[5];// 存储顺子牌
			for (int i = 0; i < 5; i++) {
				straightComnbs[i] = aAsNormal[Flag_end - 4+i];
			}
			currentMaxHand=straightComnbs;//存储将当前最大牌型
			/**
			 * 分析顺子的牌力 1.牌面3张+手牌2张 （超强牌） 【个位置为 9】 2.牌面4张+手牌1张 a.上顺/卡顺
			 * （强牌）【个位置为6】 b.下顺（次强牌）【个位置为3】 3.牌面5张 弱牌【牌力置为0】
			 */
			if (findCardsInStraight(sharedCards, straightComnbs) == 3) {// 牌面3张
																		// +手牌2张
																		// 返回 49
				return 49;
			} else {
				if (findCardsInStraight(sharedCards, straightComnbs) == 4) {
					if (holeCards.get(0).number == straightComnbs[4]
							|| holeCards.get(1).number == straightComnbs[4]
							|| (holeCards.get(0).number != straightComnbs[0] || holeCards
									.get(1).number != straightComnbs[0])) {
						return 46;// 卡顺和上顺 返回 46
					} else {
						return 43;// 下顺 返回 43
					}
				} else {
					return 40;
				}
			}
		}
		// 有A的情况
		if (hasAcard) {
			int[] aAs1 = new int[7];
			index = 0;
			for (Integer number : numberOfCards.keySet()) {
				if (number == 14)
					number = 1;
				aAs1[index] = number;
				index++;
			}
			Arrays.sort(aAs1);
			preNumber = aAs1[0];
			count = 0;
			Flag_end=0;
			for (int i = 1; i < index; i++) {
				if (aAs1[i] - preNumber == 1)
					count++;
				else {
					if (count >= 4)Flag_end=i;						
					count = 0;
				}		
				if (count >= 4)Flag_end=i;
				preNumber = aAs1[i];
				
			}
			if (Flag_end != 0) {
				int[] straightComnbs = new int[5];// 存储顺子牌
				for (int i = 0; i < 5; i++) {
					straightComnbs[i] = aAs1[Flag_end - 4+i];
				}
				currentMaxHand=straightComnbs;//存储将当前最大牌型
				/**
				 * 分析顺子的牌力 1.牌面3张+手牌2张 （超强牌） 【个位置为 9】 2.牌面4张+手牌1张 a.上顺/卡顺
				 * （强牌）【个位置为6】 b.下顺（次强牌）【个位置为3】 3.牌面5张 弱牌【 牌力置为0】
				 */
				
				if (findCardsInStraight(sharedCards, straightComnbs) == 3) {// 牌面3张
																			// +手牌2张
																			// 返回 49
					return 49;
				} else {
					if (findCardsInStraight(sharedCards, straightComnbs) == 4) {
						if (holeCards.get(0).number == straightComnbs[4]
								|| holeCards.get(1).number == straightComnbs[4]
								|| (holeCards.get(0).number != straightComnbs[0] || holeCards
										.get(1).number != straightComnbs[0])) {
							return 46;// 卡顺和上顺 返回 46
						} else {
							return 43;// 下顺 返回 43
						}
					} else {
						return 40;
					}
				}
			}
		}
		return 0;
	}
	/**
	 * 判断是否存在同花牌 50-59
	 * @return
	 */
		public  int getFlush(Map<Integer, Integer> suitOfCards ){
			int colorOfFlush=-1;//记录同花的花色
			for(Integer color:suitOfCards.keySet()){
				if(suitOfCards.get(color)>=5){
					colorOfFlush=color;
					break;
				}
			}
	/**
	 * 存在同花 则存储当前最大组合的同花牌
	 * 1.公共牌有3张同色  超强牌 【个位置为 9】
	 * 2.公共牌有4张同色 若 手牌的花色为当前能获取的同色牌的前2位 则 认为强牌	【个位置为 6】 否则 认为弱牌	【个位置为 3】	
	 * 3.公共牌有5张 当前手牌为弱牌 置为0
	 */
			if(colorOfFlush>=0){
				int[] flushComnbs=new int[5];//存储当前最大组合的同花牌
				int index=0;
				for(Card card:allCards){
					if(card.suit==colorOfFlush){
						if(index<5){
							flushComnbs[index]=card.number;
							index++;
						}else{
							Arrays.sort(flushComnbs);
							int number=card.number;
							//if(card.number==1)number=14; 
							if(number>flushComnbs[0]){
								flushComnbs[0]=number;
							}
						}
					}
				}
				Arrays.sort(flushComnbs);
				currentMaxHand=flushComnbs;//存储将当前最大牌型
				if(findCardsInFlush(sharedCards, flushComnbs, colorOfFlush)==3){
					return 59;
				}else{				
					if(findCardsInFlush(sharedCards, flushComnbs, colorOfFlush)==4){
						int holeNumber=0;
						if(holeCards.get(0).suit==colorOfFlush){
							holeNumber=holeCards.get(0).number;
						}
						//if(holeNumber==1)holeNumber=14;
						if(holeCards.get(1).suit==colorOfFlush&&holeCards.get(1).number>holeNumber){
							holeNumber=holeCards.get(1).getNumber();
						}
						//if(holeNumber==1)holeNumber=14;
						int count_biggerThanHole=0;//计数比当前手牌强的手牌数
					    for(int i=holeNumber;i<15;i++){
					    	boolean Flag_count=true;//当前值是否计数
					    	for(int j=0;j<5;j++){
					    		if(flushComnbs[j]==i){
					    			Flag_count=false;
					    			break;
					    		}
					    	}
					    	if(Flag_count)count_biggerThanHole++;
					    }
					    if(count_biggerThanHole<=2)return 56;
					    else return 53;
					}else{
						return 50;
					}
				}		
			}		
			return 0;	
		}
		/**
		 * 判断是否存在三条 30-39
		 * @return
		 */
		public  int getSet(Map<Integer, Integer> numberOfCards){
			//TODO
			int triNumber=0;
			int index=0;
			boolean Flag_hasSet=false;
			int[] highCardComnbs=new int[2];
			
			for(Integer number:numberOfCards.keySet()){
				if(numberOfCards.get(number)==3){
					triNumber=number;
					Flag_hasSet=true;
				}else{
					if(index<2){
						highCardComnbs[index]=number;
					}else{
						Arrays.sort(highCardComnbs);
						if(number>highCardComnbs[0]){
							highCardComnbs[0]=number;
						}
					}
					index++;
				}
			}
			if(Flag_hasSet){
				Arrays.sort(highCardComnbs);
				int[] setComnbs=new int[5];
				setComnbs[4]=triNumber;
				setComnbs[3]=triNumber;
				setComnbs[2]=triNumber;
				setComnbs[1]=highCardComnbs[1];
				setComnbs[0]=highCardComnbs[0];
				currentMaxHand=setComnbs;//存储将当前最大牌型
			/**
			 * set牌力分析
			 * 1.暗3   手牌两张都是triNumber 【个位置为9】
			 * 2.明3   手牌只有一张是 triNumber 【个位置为6】
			 * 3.面3   公共牌是3张 手牌是高牌A  【个位置为3】
			 * 4.面high  【个位置为1】
			 */
				Map<Integer, Integer> numbersOfSharedCrads=getNumberOfCards(sharedCards);
				if(numbersOfSharedCrads.get(triNumber)==1){
					return 39;
				}else{
					if(numbersOfSharedCrads.get(triNumber)==2){
						return 36;
					}else{
						if(Math.max(holeCards.get(0).number, holeCards.get(0).number)==14||(triNumber==14&&Math.max(holeCards.get(0).number, holeCards.get(0).number)==13)){
							return 33;
						}else{
							return 30;
						}
					}
				}
			}
			
			return 0;
		}
		
		/**
		 * 判断是否存在两队 20-29
		 * @return
		 */
		public  int getTwoPairs(Map<Integer, Integer> numberOfCards){
			int firstPairNumber=0;//较大的对子
			int secondPairNumber=0;//较小的对子
			int highCardNumber=0;//两队外的高牌
			int count_numberOfPairs=0;
			for(Integer number:numberOfCards.keySet()){
				if(numberOfCards.get(number)==2){
					//if(number==1)number=14;
					if(count_numberOfPairs<2){
						if(count_numberOfPairs==0)secondPairNumber=number;
						else{
							int tmp=secondPairNumber;
							firstPairNumber=Math.max(tmp, number);
							secondPairNumber=Math.min(tmp, number);
						}
					}else{
						int tmp1=secondPairNumber;
						int tmp2=firstPairNumber;
						firstPairNumber=Math.max(tmp2, Math.max(tmp2, number));
						if(firstPairNumber==tmp1){
							secondPairNumber=Math.max(tmp1, number);
						}else{
							secondPairNumber=tmp2;
						}
					}
					count_numberOfPairs++;
				}else{
					//if(number==1)number=14;
					if(number>highCardNumber)highCardNumber=number;
				}
			}
			if(count_numberOfPairs>=2){
				int[] twoPairsComnbs=new int[5];
				twoPairsComnbs[3]=twoPairsComnbs[4]=firstPairNumber;
				twoPairsComnbs[1]=twoPairsComnbs[2]=secondPairNumber;
				twoPairsComnbs[0]=highCardNumber;
				currentMaxHand=twoPairsComnbs;//存储将当前最大牌型
				/**
				 * 两队牌力算法
				 * 1.公共牌无对子 自己的手牌与公共牌组成对子 【个位置为9】
				 * 2.自己手上是对子且该对子比高牌大【个位置为6】 手上是对子但比高牌小或者手上没对子 但是有A【个位置为3】
				 * 3.两队和高牌都是公共面（面high）
				 */
				int numberOfAllCardsSameSuit=0;//同花色
				int colorOfsuit=-1;
				Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
				for(Integer number:suitOfCards.keySet()){
					if(suitOfCards.get(number)==4){
						numberOfAllCardsSameSuit=4;
						colorOfsuit=number;
						break;
					}
				}
				//听花
				int maxSharedCardsNumber=0;//公共面的高牌
				for(Card card:sharedCards){
					if(card.number>maxSharedCardsNumber)maxSharedCardsNumber=card.number;
				}
				if((holeCards.get(0).number==firstPairNumber&&holeCards.get(1).number==secondPairNumber)||(holeCards.get(1).number==firstPairNumber&&holeCards.get(0).number==secondPairNumber)){
					return 29;
				}else{
					if((holeCards.get(0).number==holeCards.get(1).number&&firstPairNumber>=maxSharedCardsNumber)||((holeCards.get(0).number==firstPairNumber||holeCards.get(1).number==firstPairNumber)&&firstPairNumber>=maxSharedCardsNumber)||(numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)){
						return 26;
					}else{
						if(holeCards.get(0).number==secondPairNumber||holeCards.get(1).number==secondPairNumber||holeCards.get(0).number==firstPairNumber||holeCards.get(1).number==firstPairNumber){
							return 23;
						}else{
							return 20;
						}
					}
					/*if(holeCards.get(0).number==holeCards.get(1).number&&(holeCards.get(0).number==firstPairNumber||holeCards.get(0).number==secondPairNumber)){
						if(holeCards.get(0).number>maxSharedCardsNumber) return 26;
						else return 23;
					}else{
						if(findCardsInTwoPairs(sharedCards, twoPairsComnbs)!=5&&highCardNumber==14){
							return 23;
						}else{
							return 20;
						}
					}*/
				}	
			}	
			return 0;		
		}
		/**
		 * 判断是否存在单对 10-19
		 * @return
		 */
	public  int getSinglePair(Map<Integer, Integer> numberOfCards){
		int douNumber=0;
		int[] highCardComnbs=new int[3];
		int index=0;
		boolean Flag_singlePair=false;;
		for(Integer number:numberOfCards.keySet()){
			//if(number==1)number=14;
			if(numberOfCards.get(number)==2){				
				douNumber=number;
				Flag_singlePair=true;
			}else{
				if(index<3){
					highCardComnbs[index]=number;
					
				}else{
					Arrays.sort(highCardComnbs);
					if(number>highCardComnbs[0]){
						highCardComnbs[0]=number;
					}
				}
				index++;
			}		
		}
		if(Flag_singlePair){
			Arrays.sort(highCardComnbs);
			int[] singlePairComnbs=new int[5];
			singlePairComnbs[4]=douNumber;
			singlePairComnbs[3]=douNumber;
			singlePairComnbs[2]=highCardComnbs[2];
			singlePairComnbs[1]=highCardComnbs[1];
			singlePairComnbs[0]=highCardComnbs[0];
			currentMaxHand=singlePairComnbs;//存储将当前最大牌型
			int numberOfAllCardsSameSuit=0;//同花色
			int colorOfsuit=-1;
			Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
			for(Integer number:suitOfCards.keySet()){
				if(suitOfCards.get(number)==4){
					numberOfAllCardsSameSuit=4;
					colorOfsuit=number;
					break;
				}
			}
			/**
			 * 单对牌力分析
			 * 1.超对  自己手牌是对子 并且大于最大的高牌 【个位置为9】
			 * 2.顶对  自己手牌有一张对牌 切对牌大于高牌   【个位置为6】
			 * 3.中对/底对  对子牌小于高牌  【个位置为3】
			 * 4.对子是公共对   【个位置为0】
			 */
			//听顺 
			Map<Integer, Integer> numberOfSharedCards = getNumberOfCards(sharedCards);
			int count=0;
			if(getStraightNumber(numberOfCards)==44){
				for(int i=0;i<4;i++){
					if(holeCards.get(0).number==longestStraight[i]||holeCards.get(1).number==longestStraight[i]){
						count++;
					}
				}
			}
			int maxSharedCardsNumber=0;
			for(Card card:sharedCards){
				if(card.number>maxSharedCardsNumber)maxSharedCardsNumber=card.number;
			}
			Map<Integer, Integer> numbersOfSharedCrads=getNumberOfCards(sharedCards);
			if((!numbersOfSharedCrads.containsKey(douNumber))&&douNumber>=maxSharedCardsNumber){
				return 19;
			}else{
				if(douNumber==maxSharedCardsNumber&&numbersOfSharedCrads.get(douNumber)==1||(numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)||((holeCards.get(0).number==douNumber||holeCards.get(1).number==douNumber)&&count==2&&sharedCards.size()<5)){
					return 16;
				}else{
					if(!numberOfSharedCards.containsKey(douNumber)||(numberOfSharedCards.containsKey(douNumber)&&numbersOfSharedCrads.get(douNumber)!=2)){
						return 13;
					}else{
						return 10;
					}
				}
			}
		}			
		return 0;	
	}
		/**
		 * 判断是否存在高牌
		 * 无牌力分析
		 * @param numberOfCards
		 * @return
		 */
	public  int getHighCard(Map<Integer, Integer> numberOfCards){
		
		int[] highCardComnbs=new int[5];
		int index=0;
		for(Integer number:numberOfCards.keySet()){
			if(index<5){
				highCardComnbs[index]=number;
			}else{
				Arrays.sort(highCardComnbs);
				if(number>highCardComnbs[0]){
					highCardComnbs[0]=number;
				}
			}
			index++;
		}
		Arrays.sort(highCardComnbs);
		currentMaxHand=highCardComnbs;//存储将当前最大牌型
		
		int numberOfAllCardsSameSuit=0;//同花色
		int colorOfsuit=-1;
		Map<Integer, Integer> suitOfCards = getSuitOfCards(allCards);
		for(Integer number:suitOfCards.keySet()){
			if(suitOfCards.get(number)==4){
				numberOfAllCardsSameSuit=4;
				colorOfsuit=number;
				break;
			}
		}
		//听顺 
		Map<Integer, Integer> numberOfSharedCards = getNumberOfCards(allCards);
		int count=0;
		if(getStraightNumber(numberOfSharedCards)==44){
			for(int i=0;i<4;i++){
				if(holeCards.get(0).number==longestStraight[i]||holeCards.get(1).number==longestStraight[i]){
					count++;
				}
			}
		}
		/*System.out.println(getStraightNumber(numberOfSharedCards));
		System.out.println(count);
		System.out.println(count==2&&sharedCards.size()<5);*/
		
		if((numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)&&(count==2&&sharedCards.size()<5)){
			return 9;
		}else{
			if((numberOfAllCardsSameSuit==4&&holeCards.get(0).suit==colorOfsuit&&holeCards.get(1).suit==colorOfsuit&&sharedCards.size()<5)||(count==2&&sharedCards.size()<5)){
				return 6;
			}
		}
		
		return 0;			
	}	
	//获取当前最大牌型 作为比较使用
	public  int[]  getCurrentMaxHand() {
		return currentMaxHand;
	}
	//寻找最大连续数
	public int getStraightNumber(Map<Integer, Integer> numberOfCards) {
		if (numberOfCards.size() < 4)
			return 0;
		int[] aAsNormal = new int[numberOfCards.size()];
		int index = 0;
		boolean hasAcard = false;
		for (Integer number : numberOfCards.keySet()) {
			if (number == 14)
				hasAcard = true;
			aAsNormal[index] = number;
			index++;
		}
		Arrays.sort(aAsNormal);
		int preNumber = aAsNormal[0];
		int count = 0;
		int Flag_end = 0;// 有顺子的标志 存储顺子的最大位置
		for (int i = 1; i < index; i++) {
			if (aAsNormal[i] - preNumber == 1)
				count++;
			else {
				if (count >= 3) {
					Flag_end = i;
				}
				count = 0;
			}
			if (count >= 3) {
				Flag_end = i;
			}
			preNumber = aAsNormal[i];
		}
		if (Flag_end != 0) {
			int[] straightComnbs = new int[4];// 存储顺子牌
			for (int i = 0; i < 4; i++) {
				straightComnbs[i] = aAsNormal[Flag_end - 3+i];
			}
			longestStraight=straightComnbs;
			return 44;
		}
		// 有A的情况
		if (hasAcard) {
			int[] aAs1 = new int[7];
			index = 0;
			for (Integer number : numberOfCards.keySet()) {
				if (number == 14)
					number = 1;
				aAs1[index] = number;
				index++;
			}
			Arrays.sort(aAs1);
			preNumber = aAs1[0];
			count = 0;
			Flag_end = 0;
			for (int i = 0; i < index; i++) {
				if (aAs1[i] - preNumber == 1)
					count++;
				else {
					if (count >= 3)
						Flag_end = i;
					count = 0;
				}
				if (count >= 3)
					Flag_end = i;
				preNumber = aAs1[i];

			}
			if (Flag_end != 0) {
				int[] straightComnbs = new int[4];// 存储顺子牌
				for (int i = 0; i < 4; i++) {
					straightComnbs[i] = aAsNormal[Flag_end - 3+i];
				}
				longestStraight=straightComnbs;
				return 44;
			}
		}
		return 0;
	}
	public  int findCardsInTwoPairs(List<Card> sample, int[] twoPairsComnbs) {
			int count=0;
			for(Card card:sample){
				for(int i=0;i<5;i++){
					int number=card.number;
					//if(number==1)number=14;
					if(number==twoPairsComnbs[i]){
						count++;
						break;
					}									
				}
			}
			return count;		
		}
		
	public int findCardsInFullHouse(List<Card> sample, int[] fullHouse) {
		int count=0;
		for(Card card:sample){
			for(int i=0;i<5;i++){
				int number=card.number;
				//if(number==1)number=14;
				if(card.number==fullHouse[i]){
					count++;
					break;
				}									
			}
		}
		return count;
	}

	public int findCardsInFlush(List<Card> sample, int[] flush, int colorOfFlush) {
		int count = 0;
		for (Card card : sharedCards) {
			if (card.suit == colorOfFlush) {
				int number = card.number;
				/*if (number == 1)
					card.number = 14;*/
				for (int i = 0; i < 5; i++) {
					if (number == flush[i]) {
						count++;
					}
				}
			}
		}
		return count;
	}

	public int findCardsInStraight(List<Card> sample, int[] straight) {
		int count = 0;
		Map<Integer, Integer> map = getNumberOfCards(sample);
		for (Integer number : map.keySet()) {
			for (int i = 0; i < straight.length; i++) {
				if (number == straight[i])
					count++;
			}
		}
		return count;
	}

	public Map<Integer, Integer> getNumberOfCards(List<Card> cards) {
		Map<Integer, Integer> res = new HashMap<Integer, Integer>();
		for (Card card : cards) {
			if (res.containsKey(card.number)) {
				res.put(card.number, res.get(card.number) + 1);
			} else
				res.put(card.number, 1);
		}
		return res;
	}

	public Map<Integer, Integer> getSuitOfCards(List<Card> cards) {
		Map<Integer, Integer> res = new HashMap<Integer, Integer>();
		for (Card card : cards) {
			if (res.containsKey(card.suit)) {
				res.put(card.suit, res.get(card.suit) + 1);
			} else
				res.put(card.suit, 1);
		}
		return res;
	}
}
