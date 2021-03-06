/*
 * work_wx
 * wuhen 2020/1/16.
 * Copyright (c) 2020  jianfengwuhen@126.com All Rights Reserved.
 */

package com.work.wx.db;

import com.work.wx.controller.modle.ChatModel;
import com.work.wx.controller.modle.FollowModel;
import javafx.scene.shape.Circle;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class ChatDbDao extends MongoDbDao {

    @Override
    protected Class<ChatModel> getEntityClass() {
        return ChatModel.class;
    }


    /**
     * @todo 查询企业成员和用户的聊天记录
     * @author wuhen
     * @param userId :
     * @param sendId :
     * @returns java.util.List<com.work.wx.controller.modle.ChatModel>
     * @throws
     * @date 2020/2/12 17:03
     */
    public List<ChatModel> queryUserChatList(String corpId,String userId,String sendId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("corpId").is(corpId);
        criteria.and("roomid").is("");
        criteria.orOperator(new Criteria().andOperator(Criteria.where("from").is(userId),
                Criteria.where("tolist").is(sendId)),new Criteria().andOperator(Criteria.where("from").is(sendId),
                Criteria.where("tolist").is(userId)));
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("seq")));
        return getMongoTemplate().find(query,getEntityClass());
    }



    /**
     * @todo
     * @author wuhen
     * @param chatModel :
     * @param groupField :
     * @param orderField :
     * @returns java.util.List<java.lang.String>
     * @throws
     * @date 2020/2/13 12:19
     */
    public List<String> queryGroupBy(ChatModel chatModel,String groupField,String orderField) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(getCriteriaByObject(chatModel)),
                Aggregation.sort(Sort.Direction.DESC, orderField),
                Aggregation.group(groupField));
        return getMongoTemplate().aggregate(aggregation, getEntityClass(),String.class).getMappedResults();
    }


    public List<String> queryNotFieldGroupBy(ChatModel chatModel,String groupField,String orderField,String notField) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(getCriteriaByObject(chatModel).and(notField).ne("").ne(null)),
                Aggregation.sort(Sort.Direction.DESC, orderField),
                Aggregation.group(groupField));
        return getMongoTemplate().aggregate(aggregation, getEntityClass(),String.class).getMappedResults();
    }



}
