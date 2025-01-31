package cn.campsg.practical.bubble.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public StarList createStars() {

		StarList stars = new StarList();
		for(int i=0;i<StarService.MAX_ROW_SIZE;i++){
			for(int j=0;j<StarService.MAX_COLUMN_SIZE;j++){
				stars.add(null);
			}
		}
		
		stars.set(stars.indexOf(0, 0) ,new Star(new Position(0, 0), StarType.BLUE));
		stars.set(stars.indexOf(1, 0) ,new Star(new Position(1, 0), StarType.PURPLE));
		stars.set(stars.indexOf(2, 0) ,new Star(new Position(2, 0), StarType.GREEN));
		stars.set(stars.indexOf(3, 0) ,new Star(new Position(3, 0), StarType.YELLOW));
		stars.set(stars.indexOf(4, 0) ,new Star(new Position(4, 0), StarType.BLUE));
		stars.set(stars.indexOf(5, 0) ,new Star(new Position(5, 0), StarType.PURPLE));
		stars.set(stars.indexOf(6, 0) ,new Star(new Position(6, 0), StarType.GREEN));
		stars.set(stars.indexOf(7, 0) ,new Star(new Position(7, 0), StarType.YELLOW));
		stars.set(stars.indexOf(8, 0) ,new Star(new Position(8, 0), StarType.BLUE));
		stars.set(stars.indexOf(9, 0) ,new Star(new Position(9, 0), StarType.PURPLE));
		
		// 随机让这一列产生2个相邻可消除的红色泡泡糖
		int random = new Random().nextInt(7);
		int num = new Random().nextInt(3)+2;
		
		for(int i=0;i<num;i++){
			stars.get(stars.indexOf(random+i, 0)).setType(StarType.RED);
		}

		return stars;
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
	public StarList getYMovedStars(StarList clearStars,
			StarList currentStarList) {
		/************ PRJ-BU2-JAVA-010 Task3 【3/3 start】***************/
		 StarList movedStar = new StarList();
		 int column = 0;
		 int yDis = 0;
		 StarsUtil.sort(clearStars);
		 Star star =  clearStars.get(clearStars.size() - 1);
		for(int i=star.getPosition().getRow() ;i>=0 ;i--){
			Star preStar = currentStarList.lookup(i, column);
			if(preStar == null){
				break;
			}
			else if(clearStars.existed(preStar)){
				yDis +=1;
			}
			else{
				
				Position p1 = new Position(preStar.getPosition().getRow(),preStar.getPosition().getColumn());
				Position p2 = new Position(preStar.getPosition().getRow() + yDis, preStar.getPosition().getColumn());
				MovedStar nextStar = new MovedStar(p1,preStar.getType(),p2);
				movedStar.add(nextStar);
			}
			
		}
		
		return movedStar;
		/************ PRJ-BU2-JAVA-010 Task3 【3/3 end】*****************/
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
			star = (Star) sList.lookup(row, (col - 1));
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.existed(star)) {
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
			star = (Star) sList.lookup(row, (col + 1));
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.existed(star)) {
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
			star = (Star) sList.lookup((row - 1), col);
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.existed(star)) {
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
			star = (Star) sList.lookup((row + 1), col);
			// 已经被消除的泡泡糖在界面上排列的泡泡糖列表中会以null表示
			// 已经被消除的泡泡糖与清除列表中已经存在的泡泡糖无需重复判断，否则会进入死循环。
			if (star != null && !clearStars.existed(star)) {
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

				Star star = currentStarList.lookup(row, column);

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

			if (currentStarList.lookup((StarService.MAX_ROW_SIZE - 1), column) == null)
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
