package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Criterion;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.criterion.SimpleExpression;
import org.criteria4jpa.projection.Projections;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;
@SuppressWarnings("unchecked")
public class CarparkUserServiceImpl implements CarparkUserService {

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	
	@Override
	@Transactional
	public Long saveUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		if (user.getId() == null) {
			dom.insert(user);
		} else {
			dom.save(user);
		}
		return user.getId();
	}
	@Override
	@Transactional
	public Long deleteUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		dom.remove(user.getId());
		return user.getId();
	}
	
	@Override
	public List<SingleCarparkUser> findAll() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(String name, String plateNo,String address,SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			if (!StrUtil.isEmpty(name)) {
				c.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.like("plateNo", plateNo, MatchMode.ANYWHERE));
			}
			if (willOverdue>0) {
				Date date = new DateTime(StrUtil.getTodayBottomTime(new Date())).plusDays(willOverdue).toDate();
				c.add(Restrictions.le(SingleCarparkUser.Property.validTo.name(), date));
			}
			if (!StrUtil.isEmpty(monthlyCharge)) {
				c.add(Restrictions.eq(SingleCarparkUser.Property.monthChargeId.name(), monthlyCharge.getId()));
			}
			if (!StrUtil.isEmpty(address)) {
				c.add(Restrictions.like("address", address, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(overdue)) {
				if (overdue.equals("是")) {
					c.add(Restrictions.le(SingleCarparkUser.Property.validTo.name(), new Date()));
				}else{
					c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), new Date()));
				}
			}
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public SingleCarparkUser findUserByPlateNo(String plateNO,Long carparkId) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like("plateNo", plateNO,MatchMode.ANYWHERE));
			}else{
				return null;
			}
			if (!StrUtil.isEmpty(carparkId)) {
				DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
				SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
				List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
				c.add(Restrictions.in("carpark",list));
			}
			c.setFirstResult(0);
			c.setMaxResults(1);
			SingleCarparkUser user = (SingleCarparkUser) c.getSingleResultOrNull();
			if (user!=null&&!user.getType().equals("储值")&&StrUtil.isEmpty(user.getValidTo())) {
				return null;
			}
			return user;
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findAllUserByPlateNO(String plateNO, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like("plateNo", plateNO,MatchMode.ANYWHERE));
			}else{
				return new ArrayList<>();
			}
			if (!StrUtil.isEmpty(validTo)) {
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
			}
			if (!StrUtil.isEmpty(carparkId)) {
				DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
				SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
				List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
				c.add(Restrictions.in("carpark",list));
			}
			SingleCarparkUser user = (SingleCarparkUser) c.getSingleResultOrNull();
			if (user!=null&&!user.getType().equals("储值")&&StrUtil.isEmpty(user.getValidTo())) {
				return null;
			}
			return null;
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public int sumAllUserSlotByPlateNO(String plateNO, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.eq("name", plateNO));
			}else{
				return 0;
			}
			if (!StrUtil.isEmpty(carparkId)) {
				DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
				SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
				List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
				c.add(Restrictions.in("carpark",list));
			}
			c.setProjection(Projections.sum(SingleCarparkUser.Property.carparkSlot.name()));
			Object singleResultOrNull = c.getSingleResultOrNull();
			System.out.println(singleResultOrNull.getClass());
			return 0;
		}finally{
			unitOfWork.end();
		}
	
	}
	@Override
	public List<SingleCarparkUser> findUserByMonthChargeId(Long id) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			c.add(Restrictions.eq("monthChargeId", id));
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	@Transactional
	public Long saveUserByMany(List<SingleCarparkUser> list) {
		for (SingleCarparkUser user : list) {
			DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
			if (user.getId() == null) {
				dom.insert(user);
			} else {
				dom.save(user);
			}
		}
		return list.size()*1L;
	}
	@Override
	public List<SingleCarparkUser> findUserThanIdMore(Long id,List<Long> errorIds) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			if (!StrUtil.isEmpty(errorIds)) {
				List<SimpleExpression> list=new ArrayList<>();
				for (Long long1 : errorIds) {
					SimpleExpression eq = Restrictions.eq("id", long1);
					list.add(eq);
				}
				c.add(Restrictions.or(Restrictions.gt("id", id),Restrictions.or(list.toArray(new SimpleExpression[list.size()]))));
			}else{
				c.add(Restrictions.gt("id", id));
			}
			c.setFirstResult(0);
			c.setMaxResults(50);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	
	@Override
	public List<SingleCarparkLockCar> findLockCarByPlateNO(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkLockCar.class);
			c.add(Restrictions.eq(SingleCarparkLockCar.Property.plateNO.name(), plateNO));
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	@Transactional
	public Long saveLockCar(SingleCarparkLockCar lc) {
		DatabaseOperation<SingleCarparkLockCar> dom = DatabaseOperation.forClass(SingleCarparkLockCar.class, emprovider.get());
		if (StrUtil.isEmpty(lc.getId())) {
			dom.insert(lc);
		}else{
			dom.update(lc);
		}
		return lc.getId();
	}
	@Override
	@Transactional
	public Long deleteLockCar(SingleCarparkLockCar lc) {
		DatabaseOperation<SingleCarparkLockCar> dom = DatabaseOperation.forClass(SingleCarparkLockCar.class, emprovider.get());
		dom.remove(lc);
		return lc.getId();
	}
	@Override
	@Transactional
	public Long savePrepaidUserPayHistory(SingleCarparkPrepaidUserPayHistory pph) {
		DatabaseOperation<SingleCarparkPrepaidUserPayHistory> dom = DatabaseOperation.forClass(SingleCarparkPrepaidUserPayHistory.class, emprovider.get());
		if (StrUtil.isEmpty(pph.getId())) {
			dom.insert(pph);
		}else{
			dom.update(pph);
		}
		return pph.getId();
	}
	
	@Override
	public List<SingleCarparkPrepaidUserPayHistory> findPrepaidUserPayHistoryList(int first, int max, String userName, String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindPrepaidUserPayHistoryCriteria(userName, plateNO, start, end);
			c.setFirstResult(first);
			c.setMaxResults(max);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	/**
	 * @param userName
	 * @param plateNO
	 * @param start
	 * @param end
	 * @return
	 */
	public Criteria createFindPrepaidUserPayHistoryCriteria(String userName, String plateNO, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkPrepaidUserPayHistory.class);
		if (!StrUtil.isEmpty(plateNO)) {
			c.add(Restrictions.like(SingleCarparkPrepaidUserPayHistory.Property.plateNO.name(), plateNO, MatchMode.ANYWHERE));
		}
		
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.like(SingleCarparkPrepaidUserPayHistory.Property.userName.name(), userName, MatchMode.ANYWHERE));
		}
		
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(SingleCarparkPrepaidUserPayHistory.Property.createTime.name(), start));
		}
		
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(SingleCarparkPrepaidUserPayHistory.Property.createTime.name(), end));
		}
		return c;
	}
	@Override
	public int countPrepaidUserPayHistoryList(String userName, String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindPrepaidUserPayHistoryCriteria(userName, plateNO, start, end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull==null?0:singleResultOrNull.intValue();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public SingleCarparkUser findUserById(Long userId) {
		unitOfWork.begin();
		try {
			SingleCarparkUser find = emprovider.get().find(SingleCarparkUser.class, userId);
			return find;
		} finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findUserByNameAndCarpark(String name, SingleCarparkCarpark carpark, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(name)) {
				c.add(Restrictions.like(SingleCarparkUser.Property.plateNo.name(), name,MatchMode.ANYWHERE));
			}else{
				return new ArrayList<>();
			}
			if (!StrUtil.isEmpty(validTo)) {
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
			}
			if (!StrUtil.isEmpty(carpark)) {
				DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
				SingleCarparkCarpark entityWithId = dom.getEntityWithId(carpark.getId());
				List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
				c.add(Restrictions.in("carpark",list));
			}
			List<SingleCarparkUser> resultList = c.getResultList();
			resultList=resultList.stream().filter(new Predicate<SingleCarparkUser>() {
				@Override
				public boolean test(SingleCarparkUser user) {
					return !user.getType().equals("储值")&&!StrUtil.isEmpty(user.getValidTo());
				}
			}).collect(Collectors.toList());
			return resultList;
		}finally{
			unitOfWork.end();
		}
	
	}
	@Override
	public SingleCarparkUser findUserByParkingSpace(String parkingSpace) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			c.add(Restrictions.eq(SingleCarparkUser.Property.parkingSpace.name(), parkingSpace));
			return (SingleCarparkUser) c.getSingleResultOrNull();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findUserByPlateNoLikeSize(int start, int size, String plateNO, int likeSize, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				if (likeSize<=0||likeSize>=7) {
					c.add(Restrictions.like(SingleCarparkUser.Property.plateNo.name(), plateNO,MatchMode.ANYWHERE));
				}else{
					Set<String> set = CarparkUtils.splitPlateWithIgnoreSize(plateNO, 1);
					List<Criterion>  list=new ArrayList<>();
					for (String string : set) {
						SimpleExpression like = Restrictions.like(SingleCarparkUser.Property.plateNo.name(), string,MatchMode.ANYWHERE);
						list.add(like);
					}
					c.add(Restrictions.or(list.toArray(new Criterion[list.size()])));
				}
			}else{
				return new ArrayList<>();
			}
			if (!StrUtil.isEmpty(validTo)) {
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
			}
			if (!StrUtil.isEmpty(carparkId)) {
				DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
				SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
				List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
				c.add(Restrictions.in("carpark",list));
			}
			c.setFirstResult(start);
			c.setMaxResults(size);
			List<SingleCarparkUser> resultList = c.getResultList();
			resultList=resultList.stream().filter(new Predicate<SingleCarparkUser>() {
				@Override
				public boolean test(SingleCarparkUser user) {
					return !user.getType().equals("储值")&&!StrUtil.isEmpty(user.getValidTo());
				}
			}).collect(Collectors.toList());
			return resultList;
		}finally{
			unitOfWork.end();
		}
	}
}
