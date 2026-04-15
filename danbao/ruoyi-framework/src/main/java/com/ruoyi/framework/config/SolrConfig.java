package com.ruoyi.framework.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Solr 配置类
 * 
 * @author ruoyi
 */
@Configuration
public class SolrConfig
{
    @Value("${spring.data.solr.host}")
    private String solrHost;

    /**
     * 配置 SolrClient Bean
     * 用于与 Solr 服务器进行通信
     */
    @Bean
    public SolrClient solrClient()
    {
        // 使用 HttpSolrClient 创建 Solr 客户端
        // solrHost 格式: http://127.0.0.1:8983/solr/cnpatent
        return new HttpSolrClient.Builder(solrHost).build();
    }
}
