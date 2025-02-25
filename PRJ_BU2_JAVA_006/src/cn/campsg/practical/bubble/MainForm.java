package cn.campsg.practical.bubble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import cn.campsg.practical.bubble.entity.Star;
import cn.campsg.practical.bubble.entity.StarList;
import cn.campsg.practical.bubble.service.StarService;
import cn.campsg.practical.bubble.service.StarServiceImpl;
import cn.campsg.practical.bubble.service.StarServiceTester;

/**
 * 泡泡糖窗体类，用于显示泡泡糖阵列、处理泡泡糖点击事件与动画
 * 
 * 
 * @author Frank.Chen
 * @version 1.5
 */
public class MainForm extends Application {
	
	/** 从服务端获取的完整10*10泡泡糖列表  **/
	private StarList mCurretStars = null;
	
	/** 窗体中显示泡泡糖的区域 **/
	private AnchorPane mStarForm = null;

	public static void show(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass()
					.getResource("/res/layout/main_layout.fxml"));

			// 将主布局加入到视图场景中
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);

			// 页面加载时或重新开始新的泡泡糖棋局时候初始化泡泡糖
			initGameStars(root);

			primaryStage.setTitle("消灭泡泡糖-Popstar3");
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 页面加载时或重新开始新的泡泡糖棋局时候初始化泡泡糖
	 * 
	 * @param root
	 *            窗体总布局
	 * 
	 */
	private void initGameStars(AnchorPane root) {

		// 返回窗体中显示泡泡糖的区域
		/** 环境包: 提供 **/
		mStarForm = (AnchorPane) root.lookup("#game_pane");

		/******************** PRJ-BU2-JAVA-006 ********************/
		//StarService service = new StarServiceTester();
		StarService service = new StarServiceImpl();
		mCurretStars = service.createStars();
		for(int i=0;i<mCurretStars.size();i++){
				Star star = mCurretStars.get(i);
				Label starFrame = new Label(); //创建界面泡泡糖显示控件Label
				starFrame.setPrefWidth(48);    //设置显示控件的宽度
				starFrame.setPrefHeight(48);   //设置显示控件的高度
				int row = star.getPosition().getRow();
				int column = star.getPosition().getColumn();
				starFrame.setId("s" + row + column);
				starFrame.setUserData(row + ";" + column);
				starFrame.setLayoutX(column * 48); //设置泡泡糖显示控件在界面的显示位置（x）
				starFrame.setLayoutY(row * 48); //设置泡泡糖显示控件在界面的显示位置（y）
				switch (star.getType().value()) {
			    case 0:
			        starFrame.getStyleClass().add("blue_star");
			        break;
			    case 1:
			        starFrame.getStyleClass().add("green_star");
			        break;
			    case 2:
			        starFrame.getStyleClass().add("yellow_star");
			        break;
			    case 3:
			        starFrame.getStyleClass().add("red_star");
			        break;
			    case 4:
			        starFrame.getStyleClass().add("purple_star");
			        break;
			    }
				
				mStarForm.getChildren().add(starFrame);
		}
		
		
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/******************************************************/

	}

	/**
	 * 基于接口动态创建泡泡糖服务类，服务类名保存与bean.conf配置文件中
	 * 
	 * @return 泡泡糖服务类
	 */
	private StarService getStarService() {
		//创建JVM类加载器
		ClassLoader loader = this.getClass().getClassLoader();

		//从配置文件bean.conf中读取泡泡糖服务类[类全名]
		String className = getClassName();

		try {
			//加载泡泡糖服务类
			Class<?> clazz = loader.loadClass(className);
			//动态创建并返回泡泡糖服务类的实例
			return (StarService) clazz.newInstance();
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 从配置文件bean.conf中读取泡泡糖服务类[类全名]
	 * 
	 * @return 泡泡糖服务类[类全名]
	 */
	private String getClassName() {
		// 读取bean.conf配置文件
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass()
				.getClassLoader().getResourceAsStream("bean.conf")));
		
		try {
			//读取第一行数据
			String line = br.readLine();
			//返回等号右侧的泡泡糖服务类[类全名]字符串
			return line.split("=")[1];
		} catch (IOException e) {
			return "";
		}
	}
}
