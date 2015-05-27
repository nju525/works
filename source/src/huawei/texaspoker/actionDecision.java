import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ��ע����
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
		int pokerRank=mPokerPowerAnalysis.pokerPowerRankValue();//����ǿ��ֵ
		Map<Integer, Integer> numberOfCards = getNumberOfCards(sharedCards);// ��ȡ�������еĲ�ͬ��������
		Map<Integer, Integer> suitOfCards = getSuitOfCards(sharedCards);// ��ȡ�������еĻ�ɫ��
		int numberOfPairs=0;//������
		int numberOfSet=0;//��set��
		for(Integer number:numberOfCards.keySet()){
			if(numberOfCards.get(number)==2){
				numberOfPairs++;
			}else{
				if(numberOfCards.get(number)==3){
					numberOfSet++;
				}
			}
		}
		int numberOfSameSuit=0;//ͬ��ɫ
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
		int oneCardToStraight=0;//���ų�˳
		//���ų�˳��  ���ų�˳��
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
		int twoCardToStraight=0;//���ų�˳
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
		 * ������ͺ͹�������������action
		 * 1.ͬ��˳  ��˳��˳(��λΪ9/6) raise pot��1/2-3/4 ��˳(��λΪ3) check/call  
		 * 2.4��      ��λΪ9 ��raise  pot��1/2-3/4   ���� check/fold
		 * 3.��«    ��λΪ9 ��raise  pot��1/2-3/4   ��λΪ6 check/call
		 * 4.ͬ��    �� ������û���ӣ�9 raise  pot��  1/2  6 check/call  3  check/fold������������һ������(9 raise/call 6 check/call  3 check/fold) ���������������� ��9  check/call ����check/fold��
		 * 5.˳��   ����������ͬ����  3ͬɫ check/call���� a.����raise���С С��1/3�׳� call b.�Լ�ʣ�������׳س������С��1/2 call��  4ͬɫ check/fold
		 * 		     ���������ж���  1����  ��3ͬ����һ����   2������4ͬ����һ���� 
		 *        �������������� ��4ͬɫһ����
		 * 6.����    ��˳������
		 * 7.����    ��˳������
		 * 8.һ��    ��˳������
		 * 9.����    ��˳������
		 */
		if (pokerRank >= 80) {
			// ͬ��˳����
			if (pokerRank % 10 > 3) {
				return "raise " + Math.min(potSize / 2, myRestJetton);
			} else {
				if (pokerRank % 10 == 3) {
					if (bet == 0) {
						return "check";
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
						return "check";
					} else {
						return "fold";
					}
				}
			}
		} else {
			// 4������
			if (pokerRank >= 70) {
				if (pokerRank % 10 == 9) {
					return "raise "
							+ Math.min( potSize / 2, myRestJetton);
				} else {
					if (bet == 0) {
						return "check";
					} else {
						return "fold";
					}
				}
			} else {
				// ��«����
				if (pokerRank >= 60) {
					if (pokerRank % 10 == 9) {
						return "raise "
								+ Math.min(potSize / 2, myRestJetton);
					} else {
						if (pokerRank % 10 == 6) {
							if (bet == 0) {
								return "check";
							} else {
							//��һ�μ�ע
								if(timeOfBet==1){
									if(bet<=potSize*3/5){
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
								return "check";
							} else {
								return "fold";
							}
						}
					}
				} else {
					// ͬ������
					// 4.ͬ�� �� ������û���ӣ�9 raise pot�� 1/2 6 check/call 3
					// check/fold������������һ������(9 raise/call 6 check/call 3
					// check/fold) ���������������� ��9 check/call ����check/fold��
					if (pokerRank >= 50) {
						if (numberOfPairs == 0 && numberOfSet == 0) {
							if (pokerRank % 10 == 9) {
								return "raise "
										+ Math.min(potSize / 2,
												myRestJetton);
							} else {
								if (pokerRank % 10 == 6) {
									if (bet == 0) {
										return "check";
									} else {									
										if(timeOfBet==1){
											if(bet<=potSize*3/5){
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
										return "check";
									} else {
										return "fold";
									}
								}
							}
						} else {
							// һ��������ʱ 75%��ʱ��raise
							if (numberOfPairs == 1) {
								if (pokerRank % 10 == 9) {
									double a = Math.random();
									if (a > 0.6&&timeOfBet==1&&bet==0) {
										return "raise "
												+ Math.min( potSize
														/ 3, myRestJetton);
									}else{										
										if(bet==0) return "check";
										if(timeOfBet==1){
											if(bet<=potSize*3/5){
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
												return "check";
											} else {
												if(timeOfBet==1){
													if(bet<=potSize*3/5){
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
											//��������
											if (bet == 0) {
												return "check";
											} else {
												return "fold";
											}
										}
									}							
							} else {
								if (numberOfPairs == 2 || numberOfSet > 0) {
									if (bet == 0) {
										return "check";
									} else {
										// �����С��ע ���ں���ʱ��ע   ��������
										if (bet < Math.min(1 * BB, potSize / 4)&&sharedCards.size()==5) {
											return "call";
										}
										return "fold";
									}
								}
							}
						}
					} else {
						// ˳�Ӿ���
						if (pokerRank >= 40) {
							if (numberOfPairs == 0 && numberOfSet == 0
									&& numberOfSameSuit < 3) {
								if (pokerRank % 10 == 9) {
									return "raise "
											+ Math.min( potSize / 2,
													myRestJetton);
								} else {
									if (pokerRank % 10 == 6) {
										if (bet == 0) {
											return "check";
										} else{
										if(timeOfBet==1){
											if(bet<=potSize*3/5){
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
											return "check";
										} else {
											return "fold";
										}
									}
								}
							} else {
								// һ�������� �����ǻ���ʱ 50%��ʱ��raise
								if (numberOfPairs == 1 || numberOfSameSuit == 3) {
									if (pokerRank % 10 == 9) {
										double a = Math.random();
										if (a > 0.7&&timeOfBet==1&&bet==0) {
											return "raise "
													+ Math.min(potSize / 3,
															myRestJetton);
										}
										else{
											if(bet==0) return "check";
											if(timeOfBet==1){
												if(bet<=potSize*3/5){
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
													return "check";
												} else {
													// ����С��3BB ����
													// ������׳ر���1/3���¾͸�ע ��������
													if(timeOfBet==1){
														if(bet<=potSize*3/5){
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
													return "check";
												} else {
													return "fold";
												}
											}
										
									}
								} else {
									if (numberOfPairs == 2 || numberOfSet > 0
											|| numberOfSameSuit == 4) {
										if (bet == 0) {
											return "check";
										} else {
											return "fold";
										}
									} else {
										if (bet == 0) {
											return "check";
										} else
										return "fold";
									}
								}
							}
						} else {
							// ��������
							/**
							 * set��������
							 * 1.��3   �������Ŷ���triNumber ����λ��Ϊ9��
							 * 2.��3   ����ֻ��һ���� triNumber ����λ��Ϊ6��
							 * 3.��3   ��������3�� �����Ǹ���A  ����λ��Ϊ3��
							 * 4.��high  ����λ��Ϊ1��
							 */
							if (pokerRank >= 30) {
								if(numberOfSameSuit<3&&oneCardToStraight==0){
									if(pokerRank%10==9){
										return "raise "
												+ Math.min( potSize / 2,
														myRestJetton);
									}else{
										if(pokerRank%10==6){
											double a = Math.random();
											if (a > 0.7&&timeOfBet==1&&bet==0) {
												return "raise "
														+ Math.min( potSize
																/ 3, myRestJetton);
											}else{
												if(bet==0)return "check";
												if(timeOfBet==1){
													if(bet<=potSize*3/5){
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
												return "check";
											} else {
												return "fold";
											}
										}
									}
								}else{
									if(oneCardToStraight==1||numberOfSameSuit==4){								
											if(bet==0)return "check";
											else return "fold";
									}else{
										//TODO
										if (pokerRank % 10 == 9) {
											double a = Math.random();
											if (a > 0.7&&timeOfBet==1) {
												return "raise "
														+ Math.min(potSize / 3,
																myRestJetton);
											}
											else{
												if(bet==0)return "check";
												if(timeOfBet==1){
													if(bet<=potSize*3/5){
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
														return "check";
													} else {
														// ����С��3BB ����
														// ������׳ر���1/3���¾͸�ע ��������
														if(timeOfBet==1){
															if(bet<=potSize*3/5){
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
														return "check";
													} else {
														return "fold";
													}
												}
											
										}			
									}

								}	
							} else {
								// ���Ӿ���
								if (pokerRank >= 20) {
									if(numberOfSameSuit<3&&oneCardToStraight==0){
										if(pokerRank%10==9){
											return "raise "
													+ Math.min( potSize / 2,
															myRestJetton);
										}else{
											if(pokerRank%10==6){
												double a = Math.random();
												if (a > 0.7&&timeOfBet==1&&bet==0) {
													return "raise "
															+ Math.min(potSize
																	/3, myRestJetton);
												}else{
													if(bet==0)return "check";
													if(timeOfBet==1){
														if(bet<=potSize*3/5){
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
													return "check";
												} else {
													return "fold";
												}
											}
										}
									}else{
										if(oneCardToStraight==1||numberOfSameSuit==4){
											if(bet==0)return "check";
											else return "fold";
										}else{
											//TODO
											if (pokerRank % 10 == 9) {
												double a = Math.random();
												if (a > 0.7&&timeOfBet==1&&bet==0) {
													return "raise "
															+ Math.min( potSize / 3,
																	myRestJetton);
												}
												else{
													if(bet==0)return "check";
													if(timeOfBet==1){
														if(bet<=potSize*3/5){
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
															return "check";
														} else {
															// ����С��3BB ����
															// ������׳ر���1/3���¾͸�ע ��������
															if(timeOfBet==1){
																if(bet<=potSize*3/5){
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
															return "check";
														} else {
															return "fold";
														}
													}
												
											}			
										}

									}	
								} else {
									// һ�Ծ���
									if (pokerRank >= 10) {
										if(numberOfSameSuit<3&&oneCardToStraight==0){
											if(pokerRank%10==9){
												return "raise "
														+ Math.min( potSize / 2,
																myRestJetton);
											}else{
												if(pokerRank%10==6){
													double a = Math.random();						
													if (a > 0.7&&timeOfBet==1&&bet==0) {
														return "raise "
																+ Math.min(potSize
																		/ 3, myRestJetton);
													}else{
														if(bet==0)return "check";
														else{
															if(timeOfBet==1){
																if(bet<=potSize*3/5){
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
														
													}
													
												}else{
													if (bet == 0) {
														return "check";
													} else {
														return "fold";
													}
												}
											}
										}else{
											if(oneCardToStraight==1||numberOfSameSuit==4){
												if(bet==0)return "check";
												else return "fold";
											}else{
												//TODO
												if (pokerRank % 10 == 9) {
													double a = Math.random();
													if (a > 0.7&&timeOfBet==1&&bet==0) {
														return "raise "
																+ Math.min( potSize / 3,
																		myRestJetton);
													}
													else{
														if(bet==0)return "check";
														if(timeOfBet==1){
															if(bet<=potSize*3/5){
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
																return "check";
															} else {
																// ����С��3BB ����
																// ������׳ر���1/3���¾͸�ע ��������
																if(timeOfBet==1){
																	if(bet<=potSize*3/5){
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
																return "check";
															} else {
																return "fold";
															}
														}
													
												}			
											}
										}	
									} else {
										// ���ƾ��ߣ��� ����������ϣ�
										if (pokerRank % 10 == 9) {
											return "raise "
													+ Math.min( potSize/3, myRestJetton);
										} else {
											if (pokerRank % 10 == 6) {
												if (bet == 0) {
													return "check";
												} else {
													if(timeOfBet==1){
														if(bet<=potSize*3/5){
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
													return "check";
												} else {
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

	// �жϹ������Ƿ������˳�棨���ų�˳ ˫�ų�˳��
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
		int Flag_end = 0;// ��˳�ӵı�־ �洢˳�ӵ����λ��
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
		// ��A�����
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
		int Flag_end = 0;// ��˳�ӵı�־ �洢˳�ӵ����λ��
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
			int[] straightComnbs = new int[4];// �洢˳����
			for (int i = 0; i < 4; i++) {
				straightComnbs[i] = aAsNormal[Flag_end - 4+i];
			}
			
		}
		// ��A�����
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
				int[] straightComnbs = new int[4];// �洢˳����
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
}
