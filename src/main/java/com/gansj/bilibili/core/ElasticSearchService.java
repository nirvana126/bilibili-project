//package com.gansj.bilibili.core;
//
//import com.gansj.bilibili.dao.Video;
//import com.gansj.bilibili.repository.VideoRepository;
//import org.elasticsearch.core.TimeValue;
//import org.springframework.stereotype.Service;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.text.Text;
//import org.elasticsearch.index.query.MultiMatchQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
//import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class ElasticSearchService {
//
//    @Resource
//    private VideoRepository videoRepository;
//
//    @Resource
//    private RestHighLevelClient restHighLevelClient;
//
//    public void addVideo(Video video){
//        videoRepository.save(video);
//    }
//
//    public Video getVideo(String keyword){
//        return videoRepository.findByTitleLike(keyword);
//    }
//
//    public void deleteAllVideos(){
//        videoRepository.deleteAll();
//    }
//
//    public List<Map<String,Object>> getContents(String keyword, Integer pageNumber, Integer pageSize) throws IOException {
//        String[] indexes = {"videos","userinfos"};
//        SearchRequest searchRequest = new SearchRequest(indexes);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.size(pageSize);
//        searchSourceBuilder.from(pageNumber-1);
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"title","nick","description");
//        searchSourceBuilder.query(multiMatchQueryBuilder);
//        searchRequest.source(searchSourceBuilder);
//        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        //高亮显示
//        String[] array = {"title","nick","description"};
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        for(String key:array){
//            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
//        }
//        //多个字段高亮
//        highlightBuilder.requireFieldMatch(false);
//        highlightBuilder.preTags("<span style=\"color:red\">");
//        highlightBuilder.postTags("</span>");
//        searchSourceBuilder.highlighter(highlightBuilder);
//        //执行搜索
//        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        List<Map<String,Object>> arrayList = new ArrayList<>();
//        for(SearchHit hit :searchResponse.getHits()){
//            //处理高亮字段
//            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//            Map<String,Object> sourceMap = hit.getSourceAsMap();
//            for(String key :array){
//                HighlightField highlightField = highlightFields.get(key);
//                if(highlightField !=null){
//                    Text[] fragments = highlightField.getFragments();
//                    String str = Arrays.toString(fragments);
//                    str = str.substring(1,str.length()-1);
//                    sourceMap.put(key,str);
//                }
//            }
//            arrayList.add(sourceMap);
//        }
//        return arrayList;
//    }
//}
