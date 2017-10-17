#!/bin/bash
#

package_Version='flow-web-admin'
config_Dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/webapps/flow-web-admin/WEB-INF/classes/'
destination_Dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/webapps/'
bak_dir='/opt/flow/flow-admin-tomcat-8.5.20-8280/'
project_Filename='flow-web-admin'
bak_project_Filename='flow-web-admin'
Id=""
httpUrl=""
  if [ -f "$config_Dir/param.properties" ];then
         . $config_Dir/param.properties
           Id="$id"
           httpUrl="$httpurl"
           echo "id=$Id"  
  fi
 
 rollback()
	{
	cd ${config_Dir} && ./web.sh stop
	if test $? -eq 0; then
		echo "${project_Filename} is stopped ok.."
	fi
	cd ${destination_Dir} 
	 rm -rf ${project_Filename} 
	 cd ${bak_dir}  
	 mv  ${project_Filename}  ${destination_Dir}
	 cd ${destination_Dir} 
	if [ -d /${bak_project_Filename} ]; then
		echo "rollback ${project_Filename} is succeed."
	fi
	cd ${config_Dir}
	chmod 744 *.sh
	cd ${config_Dir} && ./web.sh start
	 echo 'tomcat重启状态为:'$?
     echo "id=$Id"  
     echo "httpUrl=$httpUrl"  
	 if test $? -eq 0;then
     echo 'tomcat重启成功啦！！！！！'
	 echo -e $project_Filename $project_Filename
	 echo -e $project_Filename $project_Filename
	 echo $destination_Dir
	 echo $config_Dir
	 cd ${package_Name}
	 curl -l -H "Content-type: application/json" -X POST -d '{"id":'$Id',"status":"2","rollback":"1"}' ${httpUrl//'\:'/':'}
	 else 
      curl -l -H "Content-type: application/json" -X POST -d '{"id":'$Id',"status":"3","rollback":"1"}' ${httpUrl//'\:'/':'}
	 fi
}


main()
{
	rollback
}

main
exit 0
