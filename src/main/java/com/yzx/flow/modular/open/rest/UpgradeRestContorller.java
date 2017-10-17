package com.yzx.flow.modular.open.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.yzx.flow.common.constant.tips.ErrorTip;
import com.yzx.flow.common.constant.tips.SuccessTip;
import com.yzx.flow.common.constant.tips.Tip;
import com.yzx.flow.common.exception.BizExceptionEnum;
import com.yzx.flow.common.persistence.model.SystemVersion;
import com.yzx.flow.core.util.ToolUtil;
import com.yzx.flow.core.util.zk.ZookeeperUtil;
import com.yzx.flow.modular.job.VersionUpdateExecute;
import com.yzx.flow.modular.open.service.IOpenUpgradeService;
/**
 * 
 * 请求升级处理结果接收
 * @author wxl
 * @date 1017/08/31 15:04
 *
 */
@RestController
@RequestMapping("/openUpgrade")
public class UpgradeRestContorller {
	
	//日志记录
	protected final static Logger logger = LoggerFactory.getLogger(UpgradeRestContorller.class);
	
	@Autowired
	private IOpenUpgradeService openUpgradeService;
	
	@Autowired
	private ZookeeperUtil zookeeperUtil;
	
	@RequestMapping(value="/1.0.0/updateStatus",method=RequestMethod.POST)
	@ResponseBody
	public Tip updateUpgrade(@RequestBody Map<String, String> map) {
			try{
				Integer id = Integer.parseInt(map.get("id"));
				Integer status = Integer.parseInt(map.get("status"));
				Integer rollback = null;
				if(map.get("rollback")!=null){
					rollback = Integer.parseInt(map.get("rollback"));//是否是回滚 rollback=1 是
				}
				SystemVersion systemVersion = openUpgradeService.selectById(id);
				if(null == id || null == systemVersion) {
					logger.info("id为null");
					return new ErrorTip(BizExceptionEnum.REQUEST_NULL);
				}
				//2.成功 3.失败
				if(status==2 || status == 3){
					Integer upadgteAll = null;
					if(rollback == null){//正常更新
						upadgteAll = openUpgradeService.UpgrdeByIdAndStatus(id,status);
					}
					if(status == 3){//更新失败，记录更新数据和回滚数据
						if(rollback!=null && rollback==1){//回滚失败
							//回滚失败暂不处理
							
						}else{//正常更新失败
							//子节点数据 状态改为3
							zookeeperUtil.setDate(VersionUpdateExecute.parentNode+systemVersion.getComponent(), status+"",true);
							//获取临时节点数据，更新回滚数据
							String rollbackOrderData = null;
							try {
								rollbackOrderData = zookeeperUtil.getDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderTempNode);
							} catch (Exception e) {
								logger.debug("第一个组件:"+systemVersion.getComponent()+"升级失败");
							}
							//更新到回滚数据
							if(ToolUtil.isEmpty(rollbackOrderData)) {
								zookeeperUtil.setDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode,systemVersion.getComponent(),true);
							} else {
								zookeeperUtil.setDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode,systemVersion.getComponent()+","+rollbackOrderData,true);
							}
							//删除临时节点
							zookeeperUtil.deleteNote(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderTempNode);
						}
					}else if(status==2){//更新成功则记录回滚顺序
						if(rollback!=null && rollback==1){//回滚成功
							//回滚成功更新回滚顺序
							String data = zookeeperUtil.getDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode);
							data = data.replace(systemVersion.getComponent()+",", "");
							zookeeperUtil.setDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderNode, data,true);
							
						}else{//正常更新成功
							//删除更新顺序
							String data = zookeeperUtil.getDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode);
							data = data.replace(systemVersion.getComponent()+",", "");
							zookeeperUtil.setDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.orderNode, data,true);
							//子节点数据
							zookeeperUtil.setDate(VersionUpdateExecute.parentNode+systemVersion.getComponent(), status+"",true);
							//创建临时节点记录回滚顺序
							zookeeperUtil.createNodeAndsetDate(VersionUpdateExecute.parentNode+VersionUpdateExecute.rollbackOrderTempNode,systemVersion.getComponent());
						}
					}
					//0:更新完成，1更新未完成，2更新失败
					if(upadgteAll!=null && upadgteAll==0){
						//全部更新完成，清除zk数据
						zookeeperUtil.clearNote(VersionUpdateExecute.parentNode);
					}
					//回滚失败或完整不清理zk了，需要人工介入
					logger.info("升级消息已接收：组件id="+id+"	升级状态(2成功，3失败):"+status);
					return new SuccessTip("升级消息已接收");
				} else {
					logger.info("升级状态异常");
					return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
				return new ErrorTip(BizExceptionEnum.REQUEST_INVALIDATE);
			}
			
		
	}
}