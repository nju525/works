package huawei.texaspoker;

import java.util.ArrayList;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<Card>();

    public Deck() {
        for (int i=0;i<4;i++) {
            for (int j=1;j<=14;j++) {
                Card card = new Card(i, j);
                cards.add(card);
            }
        }
    }
    public List<Card> getCards() {
        return cards;
    }

    public Card removeTopCard() {
        return cards.remove(0);
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }
    
    public List<List<Card>> fromDeckToCouplesOfCard(){
        List<List<Card>> couplesOfCard = new ArrayList<List<Card>>();
        int i,j;
        for(i = 0; i < this.cards.size(); i++){           
            for (j = i+1; j < this.cards.size(); j++){    
                List<Card> tmpCards = new ArrayList<Card>();
                tmpCards.add(this.cards.get(i));
                tmpCards.add(this.cards.get(j));
                couplesOfCard.add(tmpCards);
            }                        
        }
        return couplesOfCard;
    }


}
