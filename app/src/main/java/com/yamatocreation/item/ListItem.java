package com.yamatocreation.item;

/**
 * Created by k0j1 on 2015/09/16.
 */
public class ListItem
{
	int mResID;
	String mSection;

	public ListItem(){

	}
	public ListItem(int nResID, String setcion){
		mResID = nResID;
		mSection = setcion;
	}

	public void SetResourceID(int nResID)
	{
		mResID = nResID;
	}
	public void SetSectionText(String setcion)
	{
		mSection = setcion;
	}

	public int GetResourceID(){ return mResID; }
	public String GetSectionText(){ return mSection; }
}
