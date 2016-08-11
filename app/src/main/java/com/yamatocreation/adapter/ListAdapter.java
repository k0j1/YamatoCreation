package com.yamatocreation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yamatocreation.R;
import com.yamatocreation.item.ListItem;

import java.util.List;


public class ListAdapter extends ArrayAdapter<ListItem>
{
	//////////////////////////////////////////////////////////////
	// 定数

	//////////////////////////////////////////////////////////////
	// データ
	protected List<ListItem> mItems;

	//////////////////////////////////////////////////////////////
	// View
	private LayoutInflater mInflater;
	private RelativeLayout mLayout;
	private ImageView mImage;
	private ImageView mNewImage;
	private TextView mTitle;
	private TextView mNews;
	private TextView mDate;
	private Typeface mFont;

	// コンストラクタ
	public ListAdapter(Context context, List<ListItem> objects)
	{
		super(context, 0, objects);
		mItems = objects;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/***********************************************************************************************
	 * void addItem()<br>
	 * アダプタにアイテムを追加<br>
	 *
	 * @return	none
	 ***********************************************************************************************/
	public void addItem(ListItem item)
	{
		mItems.add(item);
	}

	@Override
	public int getCount()
	{
		return mItems.size();
	}

	// 1行ごとのビューを生成する
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;

		// 現在参照しているリストの位置からItemを取得する
		ListItem item = this.getItem(position);

		view = mInflater.inflate(R.layout.list_item, parent, false);

		{
			view.setTag(item);
			// 背景セット
			mLayout = (RelativeLayout) view.findViewById(R.id.list_layout);
			// タイトルセット
			mTitle = (TextView) view.findViewById(R.id.list_text);
			mTitle.setText(item.GetSectionText());
			// 画像セット
			mImage = (ImageView) view.findViewById(R.id.list_image);
			mImage.setImageResource(item.GetResourceID());
		}

		return view;
	}

}