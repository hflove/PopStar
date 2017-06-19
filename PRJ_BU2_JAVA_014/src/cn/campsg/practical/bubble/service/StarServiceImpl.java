package cn.campsg.practical.bubble.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.campsg.practical.bubble.entity.MovedStar;
import cn.campsg.practical.bubble.entity.Position;
import cn.campsg.practical.bubble.entity.Star;
import cn.campsg.practical.bubble.entity.Star.StarType;
import cn.campsg.practical.bubble.entity.StarList;
import cn.campsg.practical.bubble.util.StarsUtil;

/**
 * 
 * 泡泡糖业务计算服务类，用于为界面提供以下服务<br>
 * 1. 创建屏幕画布随机泡泡糖<br>
 * 2. 切关判定<br>
 * 3. 根据消除的泡泡计算得分<br>
 * 4. 计算剩余泡泡糖的得分奖励<br>
 * 
 * @author Frank.Chen
 * @version 2.5
 *
 */
public class StarServiceImpl implements StarService {

	private Logger logger = Logger.getLogger(StarServiceImpl.class);

	/**
	 * 创建屏幕画布随机泡泡糖（10 * 10）
	 * 
	 * @return 泡泡糖列表-供画面显示
	 */
	@Override
	public StarList createBubbleMatrix() {

		StarList stars = new StarList();

		for (int row = 0; row < StarService.MAX_ROW_SIZE; row++) {

			for (int col = 0; col < StarService.MAX_COLUMN_SIZE; col++) {

				Star star = new Star();
				// 设置泡泡糖在画面上的位置
				star.setPosition(new Position(row, col));
				int typeIndex = 1;
				star.setType(StarType.valueOf(typeIndex));
				// 加入列表
				stars.add(star);
			}
		}

		return stars;
	}

	/**
	 * 以给定泡泡糖（用户点击的）为基础，向左右、上下路径依次寻找相同类型的泡泡糖
	 * 
	 * @param base
	 *            基础泡泡糖（用户点击的）
	 * @param sList
	 *            原始泡泡糖列表（界面上排列的泡泡糖）
	 * @param clearStars
	 *            待清除的泡泡糖列表
	 */
	private void findStarsByPath(Star base, StarList sList, StarList clearStars) {
		// 获取当前被点击泡泡糖的行和列
		int row = base.getPosition().getRow();
		int col = base.getPosition().getColumn();
		StarType type = base.getType();

		Star star = null;

		// 向左侧路径步进判断
		if (col - 1 >= 0) {
			// 不碰到左侧边界的情况下，获取被点击泡泡糖的左侧泡泡糖
			star = (Star) sList.findout(row, (col - 1));
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.isexisted(star)) {
				if (star.getType() == type) {
					// 被点击泡泡糖与判定泡泡保持一致时，加入列表。
					clearStars.add(StarsUtil.copy(star));
					// 继续按左侧路径步进判断。
					findStarsByPath(star, sList, clearStars);
				}
			}
		}

		// 向右侧路径步进判断
		if (col + 1 < StarService.MAX_COLUMN_SIZE) {
			// 不碰到右侧边界的情况下，获取被点击泡泡糖的右侧泡泡糖
			star = (Star) sList.findout(row, (col + 1));
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.isexisted(star)) {
				if (star.getType() == type) {
					// 被点击泡泡糖与判定泡泡保持一致时，加入列表。
					clearStars.add(StarsUtil.copy(star));
					// 继续按右侧路径步进判断。
					findStarsByPath(star, sList, clearStars);
				}
			}
		}

		// 向上方路径步进判断
		if (row - 1 >= 0) {
			// 不碰到上方边界的情况下，获取被点击泡泡糖的上方泡泡糖
			star = (Star) sList.findout((row - 1), col);
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.isexisted(star)) {
				if (star.getType() == type) {
					// 被点击泡泡糖与判定泡泡保持一致时，加入列表。
					clearStars.add(StarsUtil.copy(star));
					// 继续按上方路径步进判断。
					findStarsByPath(star, sList, clearStars);
				}
			}
		}

		// 向下方路径步进判断
		if (row + 1 < MAX_ROW_SIZE) {
			// 不碰到下方边界的情况下，获取被点击泡泡糖的下方泡泡糖
			star = (Star) sList.findout((row + 1), col);
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.isexisted(star)) {
				if (star.getType() == type) {
					// 被点击泡泡糖与判定泡泡保持一致时，加入列表。
					clearStars.add(StarsUtil.copy(star));
					// 继续按下方路径步进判断。
					findStarsByPath(star, sList, clearStars);
				}
			}
		}

		// 以上四个判断都不进，则表示四周都没用泡泡糖了，那么跳出递归方法。
	}

	/**
	 * 用户点击泡泡糖，获取满足消除条件的泡泡糖列表
	 * 
	 * @param base
	 *            被用户点击的泡泡糖
	 * @param sList
	 *            当前画面上泡泡的列表
	 * @return 需要清除的泡泡糖
	 */
	@Override
	public StarList tobeClearedStars(Star base, StarList mCurrent) {

		// 用于保存待清除的泡泡糖
		StarList clearStars = new StarList();

		// 从当前列表中获取指定行与指定列的泡泡糖（base泡泡糖）
		// 将该泡泡糖作为清除对象保存于列表中
		// 注意：所有待清除的泡泡糖都应该在原始的界面泡泡糖列表中
		clearStars.add(base);

		// 以被点击泡泡糖为基础按左右、上下不同路径寻找相同类型（颜色）的待清除泡泡糖
		findStarsByPath(base, mCurrent, clearStars);

		if (clearStars.size() == 1)
			clearStars.clear();

		if (logger.isDebugEnabled())
			logger.debug("待清除的泡泡糖内存列表:" + clearStars);

		return clearStars;
	}

	/**
	 * 消除泡泡糖后，获取待移动泡泡糖列表(仅限垂直列表的泡泡糖)<br>
	 * 该功能固定在消除被点击泡泡糖之后运行
	 * 
	 * @param clearStars
	 *            待清除的泡泡糖列表（以此作为判定待移动泡泡糖的基础）
	 * @param currentStarList
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * @return 待移动泡泡糖列表
	 */
	public StarList getVMovedStars(StarList clearStars,
			StarList currentStarList) {

		if (clearStars == null || clearStars.size() == 0)
			return null;

		// 用于保存待移动的泡泡糖
		StarList moveStars = new StarList();

		// 对待清除的泡泡糖执行分组操作
		/*
		 * 例如: 待消除的泡泡糖为：(2,1);(3,1);(4,1);(2,2);(3,2);(4,2)
		 * 分组后：1-{(2,1);(3,1);(4,1)}；2-{(2,2);(3,2);(4,2)}
		 */
		Map<Integer, StarList> groupedStars = StarsUtil.group(clearStars);

		// 按列获取所有待删除的泡泡糖（一列可能对应多行泡泡糖）
		Iterator<Integer> keys = groupedStars.keySet().iterator();
		while (keys.hasNext()) {
			// 获取列序号
			Integer column = keys.next();
			// 获取当前列所有对应需要删除的泡泡糖行
			StarList values = groupedStars.get(column);

			// 最大行数（从泡泡糖阵列底部网上搜索待移动的泡泡糖）
			int starPosition = values.lastElement().getPosition().getRow();

			// 移动间隔数量
			int span = 0;

			// 从底部往上搜索待移动的泡泡糖
			for (int row = starPosition; row >= 0; row--) {
				// 获取泡泡糖
				Star star = currentStarList.findout(row, column);
				// 虽然没有到达泡泡糖阵列顶部，但是当前列已无可消除的泡泡糖那么停止当前列搜索
				if (star == null)
					break;

				// 如果被搜索的泡泡糖在清除队列中，则表示该泡泡糖待清除无需移动
				// 那么待移动路径长度加1
				if (clearStars.isexisted(star)) {
					span++;
					continue;
				}

				MovedStar mStar = StarsUtil.toMovedStar(star);
				// 设置泡泡糖上下移动距离
				mStar.getMovedPosition().setRow(
						mStar.getPosition().getRow() + span);
				// 待修改
				mStar.getMovedPosition().setColumn(
						mStar.getPosition().getColumn());
				// 保存待移动的泡泡糖
				moveStars.add(mStar);

			}
		}
		if (logger.isDebugEnabled())
			logger.debug("待移动泡泡糖内存列表（垂直移动方向的）:" + moveStars + "待移动泡泡糖个数为："
					+ moveStars.size());

		return moveStars;
	}

	/**
	 * 消除泡泡糖后，获取待移动泡泡糖列表(仅限水平列表的泡泡糖)<br>
	 * 该功能固定在垂直列表的泡泡糖之后运行
	 * 
	 * @param currentStarList
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * 
	 * @return 待移动泡泡糖列表
	 */
	public StarList getHMovedStars(StarList currentStarList) {

		// 获取所有被清空泡泡糖的列
		List<Integer> nullColumns = getEmpyColumns(currentStarList);

		// 没有完全被清空泡泡糖的列，返回null，告知界面X轴无需移动
		if (nullColumns == null || nullColumns.size() == 0)
			return null;

		StarList moveStars = new StarList();

		// 获取判定启示列
		int starPosition = nullColumns.get(0) + 1;

		// 水平移动距离
		int span = 1;

		// 从判定启示列->右侧边界逐层判断
		for (int column = starPosition; column < StarService.MAX_COLUMN_SIZE; column++) {
			// 遇到一个列被清空则水平移动距离+1
			if (nullColumns.contains(column)) {
				span++;
				continue;
			}

			// 当前列泡泡糖如果没有被清空，则将待移动的泡泡糖加入到移动列表中
			for (int row = (StarService.MAX_ROW_SIZE - 1); row >= 0; row--) {

				Star star = currentStarList.findout(row, column);

				if (star == null)
					break;

				
				MovedStar mStar = StarsUtil.toMovedStar(star);
				// 设置泡泡糖上下移动距离
				mStar.getMovedPosition().setRow(mStar.getPosition().getRow());
				// 待修改
				mStar.getMovedPosition().setColumn(
						mStar.getPosition().getColumn() - span);
				// 保存待移动的泡泡糖
				moveStars.add(mStar);
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("待移动泡泡糖内存列表（水平移动方向的）:" + moveStars);

		return moveStars;

	}

	/**
	 * 获取被清空所有泡泡糖的列序号
	 * 
	 * @param currentStarList
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * @return 被清空所有泡泡糖的列序号集合
	 */
	private List<Integer> getEmpyColumns(StarList currentStarList) {

		List<Integer> ret = new ArrayList<Integer>();

		// 如果每列底部不存在泡泡糖（即最底部的泡泡糖不存在）那么该列视为已被清空
		for (int column = 0; column < StarService.MAX_COLUMN_SIZE; column++) {

			if (currentStarList.findout((StarService.MAX_ROW_SIZE - 1), column) == null)
				ret.add(column);
		}

		if (logger.isDebugEnabled())
			logger.debug("当前内存中被清除的空列:" + ret);

		return ret;
	}

	/**
	 * 判断是否还存在未消除的泡泡糖
	 * 
	 * @param currentStarList
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * @return true:任然有未消除的泡泡糖,false:没有未消除的泡泡糖
	 * 
	 */
	@Override
	public boolean tobeEliminated(StarList currentStarList) {

		// 待消除泡泡糖列表
		StarList clearStars = new StarList();

		for (int i = 0; i < currentStarList.size(); i++) {

			Star star = currentStarList.get(i);

			if (star != null)
				findStarsByPath(star, currentStarList, clearStars);

			// 如果待消除泡泡糖列表不等于0，则表示还有可消除的泡泡糖，返回true
			if (clearStars.size() > 0)
				return true;
		}

		if (logger.isDebugEnabled())
			logger.debug("不存在可消除的泡泡糖个数=" + getLeftStarNum(currentStarList));

		return false;
	}

	/**
	 * 获取剩余泡泡糖个数
	 * 
	 * @param mCurretStars
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * @return 剩余泡泡糖个数
	 * */
	private int getLeftStarNum(StarList currentStarList) {
		int leftStar = 0;
		// 遍历泡泡糖列表，把不为null的泡泡糖记为剩余泡泡糖
		for (int i = 0; i < currentStarList.size(); i++) {
			if (currentStarList.get(i) != null)
				leftStar++;
		}
		if (logger.isDebugEnabled())
			logger.debug("还剩余未消除的泡泡糖数量为" + leftStar);
		// 返回剩余泡泡糖数目
		return leftStar;
	}

	/**
	 * 获取无法消除泡泡糖列表
	 * 
	 * @param curretStars
	 *            当前完整的界面泡泡糖列表（已经被消除的泡泡糖用null表示）
	 * @return 无法消除泡泡糖列表
	 * */
	public StarList getAwardStarList(StarList curretStars) {
		StarList awardStarList = new StarList();

		// 遍历curretStars，把不为null的泡泡糖加到奖励泡泡糖列表中
		for (int i = 0; i < curretStars.size(); i++) {
			// tempStar = curretStars.get(i);
			if (curretStars.get(i) != null) {
				awardStarList.add(StarsUtil.copy(curretStars.get(i)));
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("还剩余未消除的泡泡糖数量为" + awardStarList);
		// 返回奖励泡泡糖列表
		return awardStarList;

	}

}
