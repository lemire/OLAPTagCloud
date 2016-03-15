package tagcloudfunction;

import java.util.Comparator;

import tagcloud.Tag;

public class CompareTags implements Comparator<Tag>{
	int mType; //Asc 0 or Desc 1
	int mAttribute; //Text 0 or Weight 1
	public CompareTags(String att, String t){
		if ("asc".equals(t.toLowerCase())) mType=0;
		else if ("desc".equals(t.toLowerCase())) mType=1;
		else throw new RuntimeException("sort type "+ t +" is not supported");
		
		if ("text".equals(att.toLowerCase())) mAttribute=0;
		else if ("weight".equals(att.toLowerCase())) mAttribute=1;
		else throw new RuntimeException("no attribute "+ att + " is known for tag elements");
	}
	
	public CompareTags(String att){
		mType=0;
		if ("text".equals(att)) mAttribute=0;
		else if ("weight".equals(att)) mAttribute=1;
		else throw new RuntimeException("no attribute "+ att + " for tag elements");
	}
	
	public CompareTags(){
		mType=0;
		mAttribute=0;
	}
	
	public int compare(Tag t, Tag s) {
		switch (mType){
			case 0:
				switch (mAttribute){
					case 0:
						return t.getText().compareTo(s.getText());
					case 1:
						if (t.getWeight() > s.getWeight()) return 1;
						else if(t.getWeight() < s.getWeight()) return -1;
						return 0;
				}
			case 1:
				switch (mAttribute){
					case 0:
						return s.getText().compareTo(t.getText());
					case 1:
						if (t.getWeight() < s.getWeight()) return 1;
						else if(t.getWeight() > s.getWeight()) return -1;
						return 0;
				}
		}
		return 0;//should not heppen
	}
}
