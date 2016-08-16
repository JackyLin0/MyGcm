<?php

  mysql_connect("sql305.er-webs.com","erweb_15945655","123456") or die("connection failed"); //資料庫連線 目的地主機需建置一組可以由外部連線(任意主機)的使用者權限 並開啟MySql Port - 3306
  mysql_select_db("erweb_15945655_project") or die("db selection failed");  // 選擇資料庫

    //避免中文亂碼
    mysql_query("SET NAMES utf8;");
    mysql_query('SET CHARACTER_SET_CLIENT=utf8;');
    mysql_query('SET CHARACTER_SET_RESULTS=utf8;');
    mysql_query("SET CHARACTER_SET_database= utf8;");

  $sql="SELECT filename FROM wp_ngg_pictures"; //選擇資料欄位及撈取資料
  
  $rows=mysql_query($sql); //聯繫資料 並指向於變數陣列
  $num=mysql_num_rows($rows); //設定計算筆數
  ?>
  
<!doctype html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes"/> <!-- 配合載具自動調整大小 -->
<meta name="apple-mobile-web-app-capable" content="yes" /> <!--應用於iOS的viewport函式-->

<title>HTC行動版網站</title>

<link href="jquery-mobile/jquery.mobile-1.4.5.min.css" rel="stylesheet" type="text/css">
<script src="jquery-mobile/jquery-2.1.3.min.js" type="text/javascript"></script>
<script src="jquery-mobile/jquery.mobile-1.4.5.min.js" type="text/javascript"></script>

<link rel="stylesheet" href="jquery-mobile/listview-grid.css"> <!-- 外掛listview-grid的CSS函數集 -->
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script> <!-- 外掛google map的script函數集 -->
<link href="./photoswipe/styles.css" type="text/css" rel="stylesheet" /> <!-- 外掛photoswipe的CSS函數集 -->
<link href="./photoswipe/photoswipe.css" type="text/css" rel="stylesheet" /> 
<script type="text/javascript" src="./photoswipe/klass.min.js"></script>  <!-- 外掛photoswipe的script函數集 -->
<script type="text/javascript" src="./photoswipe/code.photoswipe-3.0.5.min.js"></script> <!-- 要將icons一起放入同資料夾中 才能正常使用上下頁及播放和關閉的集成功能 -->
	

    <!-- PhotoSwipe 相簿型態 -->
	<script type="text/javascript">

		(function(window, PhotoSwipe){
		
			document.addEventListener('DOMContentLoaded', function(){
			
				var
					options = {},
					instance = PhotoSwipe.attach( window.document.querySelectorAll('#Gallery a'), options );
			
			}, false);
			
			
		}(window, window.Code.PhotoSwipe));
		
	</script>
    
    
</head>

<body>


<div data-role="page" id="PhotoSwipe">  
  
  <div data-role="header" data-position="fixed">
    <!-- <h1>PhotoSwipe 圖片資料存於雲端</h1> -->
  </div>
  
  <div data-role="content">
    
   <ul id="Gallery" class="gallery">
   
  <!-- 改用php撈取遠端資料庫及主機上的圖片 雲端同步-->     	 	

<?php 

  for($i=0;$i<$num;$i++) {
    //echo mysql_result($rows,$i,"filename")."<BR>";   //以mysql_result函式取得資料內容 並印出
	
	$img=mysql_result($rows,$i,"filename");
	//echo '<img src="http://linux.tcnr614.com/project/wp-content/gallery/htc/'.$img.'"/>'; //以圖片方式顯示
	echo '<li><a href="http://dbserver66.er-webs.com/project/wp-content/gallery/'.$img.'"><img src="http://dbserver66.er-webs.com/project/wp-content/gallery/'.$img.'"/></a></li>';	
  }

?>    
        	
	</ul>   
  </div>  
        
  <div data-role="footer" data-position="fixed">
    <!--<h4>頁尾</h4>-->
  </div>
    
</div>




</body>
</html>
