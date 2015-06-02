
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下注决策
 * check | call | raise num | all_in | fold eol
 */
public class actionDecision {
	private List<Card> holeCards=new ArrayList<Card>();
	private List<Card> sharedCards=new ArrayList<Card>();
	private int BB=0;
	private int potSize=0;
	private String lastAction="";
	private int bet=0;
	private int myRestJetton=0;
	private int timeOfBet=0;
	public actionDecision( List<Card> holeCards, List<Card> sharedCards ,int bet,int BB,String lastAction,int potSize,int myRestJetton,int timeOfBet){
		this.holeCards=holeCards;
		this.sharedCards=sharedCards;
		this.bet=bet;
		this.BB=BB;
		this.potSize=potSize;
		this.lastAction=lastAction;
		this.myRestJetton=myRestJetton;
		this.timeOfBet=timeOfBet;
	}
	public String actionSendToServer(){
		pokerPowerAnalysis mPokerPowerAnalysis=new pokerPowerAnalysis(holeCards, sharedCards);
		int pokerRank=mPokerPowerAnalysis.pokerPowerRankValue();//牌力强度值
		Map<Integer, Integer> numberOfCards = getNumberOfCards(sharedCards);// 获取所有牌中的不同数字数量
		Map<Integer, Integer> suitOfCards = getSuitOfCards(sharedCards);// 获取所有牌中的花色数
		int numberOfPairs=0;//共对数
		int numberOfSet=0;//公set数
		for(Integer number:numberOfCards.keySet()){
			if(numberOfCards.get(number)==2){
				numberOfPairs++;
			}else{
				if(numberOfCards.get(number)==3){
					numberOfSet++;
				}
			}
		}
		int numberOfSameSuit=0;
		for(Integer number:suitOfCards.keySet()){
			if(suitOfCards.get(number)==3){
				numberOfSameSuit=3;
			}else{
				if(suitOfCards.get(number)==4){
					numberOfSameSuit=4;
					break;
				}
			}
		}
		int oneCardToStraight=0;//单张成顺
		//单张成顺面  两张成顺面
		for(int i=2;i<14;i++){
			if(numberOfCards.containsKey(i))continue;
			else{
				numberOfCards.put(i, 1);
				if(getStraight(numberOfCards)>0){
					oneCardToStraight=1;
					break;
				}else{
					numberOfCards.remove(i);
				}
			}
		}
		int twoCardToStraight=0;//两张成顺
		for(int i=2;i<=14;i++){
			if(numberOfCards.containsKey(i))continue;
			else{
				numberOfCards.put(i, 1);
				for(int j=i+1;j<=14;j++){
					if(numberOfCards.containsKey(j))continue;
					else{
						numberOfCards.put(j, 1);
						if(getStraight(numberOfCards)>0){
							twoCardToStraight=1;
						}else{
							numberOfCards.remove(j);
						}
					}
				}
				numberOfCards.remove(i);
			}
		}	
		/**
		 * 根据牌型和公共牌面来决定action
		 * 1.同花顺  上顺卡顺(个位为9/6) raise pot的1/2-3/4 下顺(个位为3) check/call  
		 * 2.4条      个位为9 则raise  pot的1/2-3/4   否则 check/fold
		 * 3.葫芦    个位为9 则raise  pot的1/2-3/4   个位为6 check/call
		 * 4.同花    若 公共面没对子（9 raise  pot的  1/2  6 check/call  3  check/fold）若公共面有一个对子(9 raise/call 6 check/call  3 check/fold) 公共面有两个对子 （9  check/call 其他check/fold）
		 * 5.顺子   若公共面有同花面  3同色 check/call（若 a.对手raise幅度小 小于1/3底池 call b.自己剩余筹码与底池筹码比例小于1/2 call）  4同色 check/fold
		 * 		     若公共面有对子  1对子  与3同花面一样处理   2对子与4同画面一样处理 
		 *        若公共面有三条 与4同色一样处理
		 * 6.三条    与顺子类似
		 * 7.两对    与顺子类似
		 * 8.一对    与顺子类似
		 * 9.高牌    与顺子类似
		 */
		if (pokerRank >= 80) {
			// 同花顺决策
			if (pokerRank % 10 > 3) {
				return "raise " + Math.min(potSize*3/5, myRestJetton);
			} else {
				if (pokerRank % 10 == 3) {
					if (bet == 0) {
						if(potSize<15*BB){
							return "raise "+BB*(sharedCards.size()-2);//bluff咋呼小底池
						}else{
							return "check";
						}
					} else {
						if(timeOfBet==1){
							if(bet<=potSize*1/3){
								if (bet < myRestJetton)
									return "call";
								else
									return "all_in";
							}else{
								if(myRestJetton<=5*BB){
									return "call";
								}else
								return "fold";
							}
						}else{	
							if(myRestJetton<=5*BB){
								return "call";
							}else
							return "fold";
						}
					}
				} else {
					if (bet == 0) {
						if(potSize<15*BB){
							return "raise "+41;//bluff咋呼小底池
						}else{
							return "check";
						}
					} else {
						if(bet<2*BB&&timeOfBet==1){
							return "raise"+41;
						}else
						return "fold";
					}
				}
			}
		} else {
			// 4条决策
			if (pokerRank >= 70) {
				if (pokerRank % 10 == 9) {
					return "raise "
							+ Math.min( potSize*3/5, myRestJetton);
				} else {
					if (bet == 0) {
						if(potSize<15*BB){
							return "raise "+41;//bluff咋呼小底池
						}else{
							return "check";
						}
					} else {
						if(bet<2*BB&&timeOfBet==1){
							return "raise"+41;
						}else
						return "fold";
					}
				}
			} else {
				// 葫芦决策
				if (pokerRank >= 60) {
					if (pokerRank % 10 == 9) {
						return "raise "
								+ Math.min(potSize*3/5, myRestJetton);
					} else {
						if (pokerRank % 10 == 6) {
							if (bet == 0) {
								if(potSize<15*BB){
									return "raise "+41;//bluff咋呼小底池
								}else{
									return "check";
								}
							} else {	
								if(timeOfBet==1){
									if(bet<=potSize*2/7){
										if (bet < myRestJetton)
											return "call";
										else
											return "all_in";
									}else{
										if(myRestJetton<=5*BB){
											return "call";
										}else
										return "fold";
									}
								}else{	
									if(myRestJetton<=5*BB){
										return "call";
									}else
									return "fold";
								}
							}
						} else {
							if (bet == 0) {
								if(potSize<15*BB){
									return "raise "+41;//bluff咋呼小底池
								}else{
									return "check";
								}
							} else {
								if(bet<2*BB&&timeOfBet==1){
									return "raise"+41;
								}else
								return "fold";
							}
						}
					}
				} else {
					// 同花决策
					// 4.同花 若 公共面没对子（9 raise pot的 1/2 6 check/call 3
					// check/fold）若公共面有一个对子(9 raise/call 6 check/call 3
					// check/fold) 公共面有两个对子 （9 check/call 其他check/fold）
					if (pokerRank >= 50) {
						if (numberOfPairs == 0 && numberOfSet == 0) {
							if (pokerRank % 10 == 9) {
								return "raise "
										+ Math.min(potSize *3/5,
												myRestJetton);
							} else {
								if (pokerRank % 10 == 6) {
									if (bet == 0) {
										if(potSize<15*BB){
											return "raise "+41;//bluff咋呼小底池
										}else{
											return "check";
										}
									} else {									
										if(timeOfBet==1){
											if(bet<=potSize*2/7){
												if (bet < myRestJetton)
													return "call";
												else
													return "all_in";
											}else{
												if(myRestJetton<=5*BB){
													return "call";
												}else
												return "fold";
											}
										}else{	
											if(myRestJetton<=5*BB){
												return "call";
											}else
											return "fold";
										}
									}
								} else {
									if (bet == 0) {
										if(potSize<15*BB){
											return "raise "+41;//bluff咋呼小底池
										}else{
											return "check";
										}
									} else {
										if(bet<2*BB&&timeOfBet==1){
											return "raise"+41;
										}else
										return "fold";
									}
								}
							}
						} else {
							// 一个公共对时 75%的时候raise
							if (numberOfPairs == 1) {
								if (pokerRank % 10 == 9) {
									double a = Math.random();
									if (a > 0.4&&timeOfBet==1&&bet==0) {
										return "raise "
												+ Math.min( potSize
														/ 3, myRestJetton);
									}else{										
										if(bet==0){
											if(potSize<15*BB){
												return "raise "+(2.03+a/10)*BB;//bluff咋呼小底池
											}else{
												return "check";
											}
										}
										if(timeOfBet==1){
											if(bet<=potSize*2/7){
												if (bet < myRestJetton)
													return "call";
												else
													return "all_in";
											}else{
												if(myRestJetton<=5*BB){
													return "call";
												}else
												return "fold";
											}
										}else{	
											if(myRestJetton<=5*BB){
												return "call";
											}else
											return "fold";
										}
									}
								}else {
										if (pokerRank % 10 == 6) {
											if (bet == 0) {
												if(potSize<15*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											} else {
												if(timeOfBet==1){
													if(bet<=potSize*2/7){
														if (bet < myRestJetton)
															return "call";
														else
															return "all_in";
													}else{
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
												}else{	
													if(myRestJetton<=5*BB){
														return "call";
													}else
													return "fold";
												}
											}
										} else {
											
											if (bet == 0) {
												if(potSize<15*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											} else {
												if(bet<2*BB&&timeOfBet==1){
													return "raise"+41;
												}else
												return "fold";
											}
										}
									}							
							} else {
								if (numberOfPairs == 2 || numberOfSet > 0) {
									if (bet == 0) {
										if(potSize<15*BB){
											return "raise "+41;//bluff咋呼小底池
										}else{
											return "check";
										}
									} else {										
										if (bet <= Math.min(1 * BB, potSize / 4)&&sharedCards.size()==5) {
											return "call";
										}else{
											if(bet<2*BB&&timeOfBet==1){
												return "raise"+41;
											}else
											return "fold";
										}
										
									}
								}
							}
						}
					} else {
						// 顺子决策
						if (pokerRank >= 40) {
							if (numberOfPairs == 0 && numberOfSet == 0
									&& numberOfSameSuit < 3) {
								if (pokerRank % 10 == 9) {
									return "raise "
											+ Math.min( potSize *3/5,
													myRestJetton);
								} else {
									if (pokerRank % 10 == 6) {
										if (bet == 0) {
											if(potSize<15*BB){
												return "raise "+41;//bluff咋呼小底池
											}else{
												return "check";
											}
										} else{
										if(timeOfBet==1){
											if(bet<=potSize*2/7){
												if (bet < myRestJetton)
													return "call";
												else
													return "all_in";
											}else{
												if(myRestJetton<=5*BB){
													return "call";
												}else
												return "fold";
											}
										}else{	
											if(myRestJetton<=5*BB){
												return "call";
											}else
											return "fold";
										}
										}
									} else {
										if (bet == 0) {
											if(potSize<15*BB){
												return "raise "+41;//bluff咋呼小底池
											}else{
												return "check";
											}
										} else {
											if(bet<2*BB&&timeOfBet==1){
												return "raise"+41;
											}else
											return "fold";
										}
									}
								}
							} else {
								// 一个公共对 或者是花面时 50%的时候raise
								if (numberOfPairs == 1 || numberOfSameSuit == 3) {
									if (pokerRank % 10 == 9) {
										double a = Math.random();
										if (a > 0.7&&timeOfBet==1&&bet==0) {
											return "raise "
													+ Math.min(potSize / 3,
															myRestJetton);
										}
										else{
											if(bet==0){
												if(potSize<15*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											}
											if(timeOfBet==1){
												if(bet<=potSize*2/7){
													if (bet < myRestJetton)
														return "call";
													else
														return "all_in";
												}else{
													if(myRestJetton<=5*BB){
														return "call";
													}else
													return "fold";
												}
											}else{	
												if(myRestJetton<=5*BB){
													return "call";
												}else
												return "fold";
											}
										}
									}else {
											if (pokerRank % 10 == 6) {
												if (bet == 0) {
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												} else {
													// 筹码小于3BB 或者
													// 筹码与底池比例1/2以下就跟注 否则弃牌
													if(timeOfBet==1){
														if(bet<=potSize*2/7){
															if (bet < myRestJetton)
																return "call";
															else
																return "all_in";
														}else{
															if(myRestJetton<=5*BB){
																return "call";
															}else
															return "fold";
														}
													}else{	
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
												}
											} else {
												return "fold";
											}
										
									}
								} else {
									if (numberOfPairs == 2 || numberOfSet > 0
											|| numberOfSameSuit == 4) {
										if (bet == 0) {
											if(potSize<15*BB){
												return "raise "+41;//bluff咋呼小底池
											}else{
												return "check";
											}
										} else {
											if(bet<=potSize*1/6&&sharedCards.size()==5&&pokerRank%10==9){
												return "call";
											}else{
												if(bet<2*BB&&timeOfBet==1){
													return "raise"+41;
												}else
												return "fold";
											}
										}
									} else {
										if (bet == 0) {
											if(potSize<15*BB){
												return "raise "+41;//bluff咋呼小底池
											}else{
												return "check";
											}
										} else
										return "fold";
									}
								}
							}
						} else {
							/**
							 * set牌力分析
							 * 1.暗3   手牌两张都是triNumber 【个位置为9】
							 * 2.明3   手牌只有一张是 triNumber 【个位置为6】
							 * 3.面3   公共牌是3张 手牌是高牌A  【个位置为3】
							 * 4.面high  【个位置为1】
							 */
							if (pokerRank >= 30) {
								if(numberOfSameSuit<3&&oneCardToStraight==0){
									if(pokerRank%10==9){
										return "raise "
												+ Math.min( potSize*3/4,
														myRestJetton);
									}else{
										if(pokerRank%10==6){
											double a = Math.random();
											if (a > 0.7&&timeOfBet==1&&bet==0) {
												return "raise "
														+ Math.min( 2*(sharedCards.size()-1)*BB, myRestJetton);
											}else{
												if(bet==0){
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												}
												if(timeOfBet==1){
													if(bet<=potSize*2/7){
														if (bet < myRestJetton)
															return "call";
														else
															return "all_in";
													}else{
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
												}else{	
													if(myRestJetton<=5*BB){
														return "call";
													}else
													return "fold";
												}
											}
											
										}else{
											if (bet == 0) {
												if(potSize<15*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											} else {
												if(bet<2*BB&&timeOfBet==1){
													return "raise"+41;
												}else
												return "fold";
											}
										}
									}
								}else{
									if(oneCardToStraight==1||numberOfSameSuit==4){								
											if(bet==0){
												if(potSize<15*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											}
											else {
												if(bet<=potSize*1/6&&sharedCards.size()==5&&pokerRank%10==9){
													return "call";
												}else{
													if(bet<2*BB&&timeOfBet==1){
														return "raise"+41;
													}else
													return "fold";
												}
											}
									}else{
										//TODO
										if (pokerRank % 10 == 9) {
											double a = Math.random();
											if (a > 0.7&&timeOfBet==1&&bet==0) {
												return "raise "
														+ Math.min((sharedCards.size()-1)*BB,
																myRestJetton);
											}
											else{
												if(bet==0){
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												}
												if(timeOfBet==1){
													if(bet<=potSize*2/7){
														if (bet < myRestJetton)
															return "call";
														else
															return "all_in";
													}else{
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
												}else{	
													if(myRestJetton<=5*BB){
														return "call";
													}else
													return "fold";
												}
											}
										}else {
												if (pokerRank % 10 == 6) {
													if (bet == 0) {
														if(potSize<15*BB){
															return "raise "+41;//bluff咋呼小底池
														}else{
															return "check";
														}
													} else {
														
														if(timeOfBet==1){
															if(bet<=potSize*1/2){
																if (bet < myRestJetton)
																	return "call";
																else
																	return "all_in";
															}else{
																if(myRestJetton<=5*BB){
																	return "call";
																}else
																return "fold";
															}
														}else{	
															if(myRestJetton<=5*BB){
																return "call";
															}else
															return "fold";
														}

													}
												} else {
													if (bet == 0) {
														if(potSize<15*BB){
															return "raise "+41;//bluff咋呼小底池
														}else{
															return "check";
														}
													} else {
														if(bet<2*BB&&timeOfBet==1){
															return "raise"+41;
														}else
														return "fold";
													}
												}
											
										}			
									}

								}	
							} else {
								// 两队决策
								if (pokerRank >= 20) {
									if(numberOfSameSuit<3&&oneCardToStraight==0&&twoCardToStraight==0){
										if(pokerRank%10==9){
											if(timeOfBet==1&&potSize<15*BB){
												return "raise "
														+ Math.min( (sharedCards.size())*BB,
																myRestJetton);
											}else{
												return "call";
											}
										}else{
											if(pokerRank%10==6){
												double a = Math.random();
												if (a > 0.7&&timeOfBet==1&&bet==0) {
													return "raise "
															+ Math.min((sharedCards.size()-1)*BB, myRestJetton);
												}else{
													if(bet==0){
														if(potSize<15*BB){
															return "raise "+41;//bluff咋呼小底池
														}else{
															return "check";
														}
													}
													if(timeOfBet==1){
														if(bet<=potSize*1/2){
															if (bet < myRestJetton)
																return "call";
															else
																return "all_in";
														}else{
															if(myRestJetton<=5*BB){
																return "call";
															}else
															return "fold";
														}
													}else{	
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
												}
												
											}else{
												if (bet == 0) {
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												} else {
													if(bet<2*BB&&timeOfBet==1){
														return "raise"+41;
													}else
													return "fold";
												}
											}
										}
									}else{
										if(oneCardToStraight==1||numberOfSameSuit==4){
											if(bet==0){
												if(potSize<20*BB){
													return "raise "+41;//bluff咋呼小底池
												}else{
													return "check";
												}
											}
											else{
												if(bet<=potSize*1/6&&sharedCards.size()==5&&pokerRank%10==9){
													return "call";
												}else{
													if(bet<2*BB&&timeOfBet==1){
														return "raise"+41;
													}else
													return "fold";
												}
											}
										}else{
											//TODO
											if (pokerRank % 10 == 9) {
												double a = Math.random();
												if (a > 0.7&&timeOfBet==1&&bet==0) {
													return "raise "
															+ Math.min( (sharedCards.size()-1)*BB,
																	myRestJetton);
												}
												else{
													if(bet==0){
														if(potSize<15*BB){
															return "raise "+41;//bluff咋呼小底池
														}else{
															return "check";
														}
													}else
													if(bet<=potSize*2/7){
														if (bet < myRestJetton)
															return "call";
														else
															return "all_in";
													}else{
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}
													/*if(timeOfBet==1){
														if(bet<=potSize*2/7){
															if (bet < myRestJetton)
																return "call";
															else
																return "all_in";
														}else{
															if(myRestJetton<=5*BB){
																return "call";
															}else
															return "fold";
														}
													}else{	
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";*/
													}
												
											}else {
													if (pokerRank % 10 == 6) {
														if (bet == 0) {
															if(potSize<15*BB){
																return "raise "+41;//bluff咋呼小底池
															}else{
																return "check";
															}
														} else {
															if(bet<=potSize*2/7){
																if (bet < myRestJetton)
																	return "call";
																else
																	return "all_in";
															}else{
																if(myRestJetton<=5*BB){
																	return "call";
																}else
																return "fold";
															}
															/*if(timeOfBet==1){
																if(bet<=potSize*1/2){
																	if (bet < myRestJetton)
																		return "call";
																	else
																		return "all_in";
																}else{
																	if(myRestJetton<=5*BB){
																		return "call";
																	}else
																	return "fold";
																}
															}else{	
																if(myRestJetton<=5*BB){
																	return "call";
																}else
																return "fold";
															}*/

														}
													} else {
														if (bet == 0) {
															if(potSize<15*BB){
																return "raise "+41;//bluff咋呼小底池
															}else{
																return "check";
															}
														} else {
															if(bet<2*BB&&timeOfBet==1){
																return "raise"+41;
															}else
															return "fold";
														}
													}
												
											}			
										}

									}	
								} else {
									// 一对决策
									if (pokerRank >= 10) {
										if(numberOfSameSuit<3&&oneCardToStraight==0&&potSize<15*BB){
											if(pokerRank%10==9){
												if(timeOfBet==1&&bet==0){
													return "raise "
															+ Math.min((sharedCards.size()-1)*BB,
																	myRestJetton);
												}else{
													if(bet==0){
														return "check";
													}else{
														if(bet<potSize*2/7)
															return "call";
														else
														return "fold";
													}
													
												}												
											}else{
												if(pokerRank%10==6){
													double a = Math.random();						
													if (a > 0.7&&timeOfBet==1&&bet==0) {
														return "raise "
																+ Math.min((sharedCards.size()-1)*BB, myRestJetton);
													}else{
														if(bet==0){
															if(potSize<15*BB){
																return "raise "+41;//bluff咋呼小底池
															}else{
																return "check";
															}
														}
														else{
															if(bet<=potSize*2/7){
																if (bet < myRestJetton)
																	return "call";
																else
																	return "all_in";
															}else{
																if(myRestJetton<=5*BB){
																	return "call";
																}else
																return "fold";
															}
														}
														
													}
													
												}else{
													if (bet == 0) {
														if(potSize<15*BB){
															return "raise "+41;//bluff咋呼小底池
														}else{
															return "check";
														}
													} else {
														if(bet<2*BB&&timeOfBet==1){
															return "raise"+41;
														}else
														return "fold";
													}
												}
											}
										}else{
											if(oneCardToStraight==1||numberOfSameSuit==4){
												if(bet==0){
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												}
												else {
													if(bet<=potSize*1/6&&sharedCards.size()==5){
														return "call";
													}else{
														if(bet<2*BB&&timeOfBet==1){
															return "raise"+41;
														}else
														return "fold";
													}
												}
											}else{
												//TODO
												if (pokerRank % 10 == 9) {
													double a = Math.random();
													if (a > 0.7&&timeOfBet==1&&bet==0) {
														return "raise "
																+ Math.min( (sharedCards.size()-1)*BB,
																		myRestJetton);
													}
													else{
														if(bet==0){
															if(potSize<15*BB){
																return "raise "+41;//bluff咋呼小底池
															}else{
																return "check";
															}
														}
														if(timeOfBet==1){
															if(bet<=potSize*2/7){
																if (bet < myRestJetton)
																	return "call";
																else
																	return "all_in";
															}else{
																if(myRestJetton<=5*BB){
																	return "call";
																}else
																return "fold";
															}
														}else{	
															if(myRestJetton<=5*BB){
																return "call";
															}else
															return "fold";
														}
													}
												}else {
														if (pokerRank % 10 == 6) {
															if (bet == 0) {
																if(potSize<15*BB){
																	return "raise "+41;//bluff咋呼小底池
																}else{
																	return "check";
																}
															} else {
																if(bet<=potSize*2/7){
																	if (bet < myRestJetton)
																		return "call";
																	else
																		return "all_in";
																}else{
																	if(myRestJetton<=5*BB){
																		return "call";
																	}else
																	return "fold";
																}
																/*if(timeOfBet==1){
																	if(bet<=potSize*1/3){
																		if (bet < myRestJetton)
																			return "call";
																		else
																			return "all_in";
																	}else{
																		if(myRestJetton<=5*BB){
																			return "call";
																		}else
																		return "fold";
																	}
																}else{	
																	if(myRestJetton<=5*BB){
																		return "call";
																	}else
																	return "fold";
																}*/

															}
														} else {
															if (bet == 0) {
																if(potSize<15*BB){
																	return "raise "+41;//bluff咋呼小底池
																}else{
																	return "check";
																}
															} else {
																if(bet<2*BB&&timeOfBet==1){
																	return "raise"+41;
																}else
																return "fold";
															}
														}
													
												}			
											}
										}	
									} else {
										// 高牌决策（包含 各种听牌组合）
										if (pokerRank % 10 == 9) {
											if(timeOfBet==1&&bet==0){
												return "raise "
														+ Math.min((sharedCards.size()-1)*BB,
																myRestJetton);
											}else{
												if(bet==0){
													return "check";
												}else{
													if(bet<potSize*2/7)
														return "call";
													else
													return "fold";
												}
												
											}
										} else {
											if (pokerRank % 10 == 6) {
												if (bet == 0) {
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												} else {
													if(bet<=potSize*2/7){
														if (bet < myRestJetton)
															return "call";
														else
															return "all_in";
													}else{
														if(myRestJetton<=5*BB){
															return "call";
														}else
														return "fold";
													}													
												}
											} else {
												if (bet == 0) {
													if(potSize<15*BB){
														return "raise "+41;//bluff咋呼小底池
													}else{
														return "check";
													}
												} else {
													if(bet<2*BB&&timeOfBet==1){
														return "raise"+41;
													}else
													return "fold";
												}
											}
										}
																				
									}
								}
							}
						}
					}
				}
			}

		}

		return "check";
	}

	// 判断公共牌是否存在听顺面（单张成顺 双张成顺）
	public int getStraight(Map<Integer, Integer> numberOfCards) {
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
		int Flag_end = 0;// 有顺子的标志 存储顺子的最大位置
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
					if (count >= 4)
						Flag_end = i;
					count = 0;
				}
				if (count >= 4)
					Flag_end = i;
				preNumber = aAs1[i];

			}
			if (Flag_end != 0) {
				return 44;
			}
		}
		return 0;
	}
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
		for (int i = 0; i < index; i++) {
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
				straightComnbs[i] = aAsNormal[Flag_end - 4+i];
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
					straightComnbs[i] = aAsNormal[Flag_end - 4+i];
				}
			}
		}
		return 0;
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
	/*public String bluffBet(){
		if(bet==0){
			return "raise "+BB*(sharedCards.size()-2);
		}else{
			
		}
	}*/
}

