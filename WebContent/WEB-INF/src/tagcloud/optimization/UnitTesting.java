package tagcloud.optimization;

import tagcloud.*;

import java.util.*;

public class UnitTesting {
	public static void main(String[]args) {
		testMonteCarlo();
	}
	public static void testMonteCarlo() {
		Vector<Tag> list = new Vector<Tag>();
		list.add(new Tag("5",1));
		list.add(new Tag("6",1));
		list.add(new Tag("7",1));
		list.add(new Tag("8",1));
		list.add(new Tag("9",1));
		list.add(new Tag("1",1));
		list.add(new Tag("2",1));
		list.add(new Tag("3",1));
		list.add(new Tag("4",1));
		TagSimilarity ts = new TagSimilarity() {
			public double similarity(Tag t, Tag s) {
				return Math.abs(Double.parseDouble(t.getText())
						- Double.parseDouble(s.getText()));
			}
		};
		PointWiseMonteCarlo [] pwmc = new PointWiseMonteCarlo[20];
		for(int k = 0; k <pwmc.length;++k) {
			System.out.println("(point-wise monte carlo) using "+k*100+" iterations");
			pwmc[k] = new PointWiseMonteCarlo(ts,k*100);
			double costbefore = pwmc[k].cost(list);
			List<Tag> newlist = pwmc[k].optimize((List<Tag>)list.clone());
			double costafter = pwmc[k].cost(newlist);
			System.out.println("before: "+costbefore+" after: "+costafter);
		}		
		MonteCarlo[] mc = new MonteCarlo[20];
		for(int k = 0; k <mc.length;++k) {
			System.out.println("(monte carlo) using "+k+" iterations");
			mc[k] = new MonteCarlo(ts,k);
			double costbefore = mc[k].cost(list);
			List<Tag> newlist = mc[k].optimize((List<Tag>)list.clone());
			double costafter = mc[k].cost(newlist);
			System.out.println("before: "+costbefore+" after: "+costafter);
		}
		ExhaustiveCut[] ec = new ExhaustiveCut[20];
		for(int k = 0; k <ec.length;++k) {
			System.out.println("(exhaustive cut) using "+k+" iterations");
			ec[k] = new ExhaustiveCut(ts,k);
			double costbefore = ec[k].cost(list);
			List<Tag> newlist = ec[k].optimize((List<Tag>)list.clone());
			double costafter = ec[k].cost(newlist);
			System.out.println("before: "+costbefore+" after: "+costafter);
		}
		
	}
}