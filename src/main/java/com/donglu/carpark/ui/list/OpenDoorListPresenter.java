package com.donglu.carpark.ui.list;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.google.inject.Inject;

public class OpenDoorListPresenter extends AbstractListPresenter<SingleCarparkOpenDoorLog>{
	private OpenDoorLogListView view;
	private String operaName;
	private Date start;
	private Date end;
	private String deviceName;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void refresh() {
		List<SingleCarparkOpenDoorLog> findByNameOrPlateNo = sp.getCarparkInOutService().findOpenDoorLogBySearch(operaName,start,end,deviceName);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(findByNameOrPlateNo.size());
	}

	
	public void search(String operaName, Date start, Date end, String deviceName) {
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		this.deviceName=deviceName;
		refresh();
	}
	@Override
	public void mouseDoubleClick(List<SingleCarparkOpenDoorLog> list) {
		SingleCarparkOpenDoorLog singleCarparkOpenDoorLog = list.get(0);
		ImageDialog id=new ImageDialog(singleCarparkOpenDoorLog.getImage());
		id.open();
	}
	@Override
	protected View createView(Composite c) {
		view=new OpenDoorLogListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("手动抬杆记录");
		return view;
	}
}
