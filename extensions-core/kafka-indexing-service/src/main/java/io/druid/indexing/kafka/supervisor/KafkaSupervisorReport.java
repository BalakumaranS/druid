/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.druid.indexing.kafka.supervisor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.druid.indexing.overlord.supervisor.SupervisorReport;
import io.druid.java.util.common.IAE;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class KafkaSupervisorReport extends SupervisorReport
{
  public static class KafkaSupervisorReportPayload
  {
    private final String dataSource;
    private final String topic;
    private final Integer partitions;
    private final Integer replicas;
    private final Long durationSeconds;
    private final List<TaskReportData> activeTasks;
    private final List<TaskReportData> publishingTasks;
    private final Map<Integer, Long> latestOffsets;
    private final Map<Integer, Long> minimumLag;
    private final Long aggregateLag;
    private final DateTime offsetsLastUpdated;

    public KafkaSupervisorReportPayload(
        String dataSource,
        String topic,
        Integer partitions,
        Integer replicas,
        Long durationSeconds,
        @Nullable Map<Integer, Long> latestOffsets,
        @Nullable Map<Integer, Long> minimumLag,
        @Nullable Long aggregateLag,
        @Nullable DateTime offsetsLastUpdated
    )
    {
      this.dataSource = dataSource;
      this.topic = topic;
      this.partitions = partitions;
      this.replicas = replicas;
      this.durationSeconds = durationSeconds;
      this.activeTasks = Lists.newArrayList();
      this.publishingTasks = Lists.newArrayList();
      this.latestOffsets = latestOffsets;
      this.minimumLag = minimumLag;
      this.aggregateLag = aggregateLag;
      this.offsetsLastUpdated = offsetsLastUpdated;
    }

    @JsonProperty
    public String getDataSource()
    {
      return dataSource;
    }

    @JsonProperty
    public String getTopic()
    {
      return topic;
    }

    @JsonProperty
    public Integer getPartitions()
    {
      return partitions;
    }

    @JsonProperty
    public Integer getReplicas()
    {
      return replicas;
    }

    @JsonProperty
    public Long getDurationSeconds()
    {
      return durationSeconds;
    }

    @JsonProperty
    public List<TaskReportData> getActiveTasks()
    {
      return activeTasks;
    }

    @JsonProperty
    public List<TaskReportData> getPublishingTasks()
    {
      return publishingTasks;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<Integer, Long> getLatestOffsets()
    {
      return latestOffsets;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<Integer, Long> getMinimumLag()
    {
      return minimumLag;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long getAggregateLag()
    {
      return aggregateLag;
    }

    @JsonProperty
    public DateTime getOffsetsLastUpdated()
    {
      return offsetsLastUpdated;
    }

    @Override
    public String toString()
    {
      return "{" +
             "dataSource='" + dataSource + '\'' +
             ", topic='" + topic + '\'' +
             ", partitions=" + partitions +
             ", replicas=" + replicas +
             ", durationSeconds=" + durationSeconds +
             ", active=" + activeTasks +
             ", publishing=" + publishingTasks +
             (latestOffsets != null ? ", latestOffsets=" + latestOffsets : "") +
             (minimumLag != null ? ", minimumLag=" + minimumLag : "") +
             (aggregateLag != null ? ", aggregateLag=" + aggregateLag : "") +
             (offsetsLastUpdated != null ? ", offsetsLastUpdated=" + offsetsLastUpdated : "") +
             '}';
    }
  }

  private final KafkaSupervisorReportPayload payload;

  public KafkaSupervisorReport(
      String dataSource,
      DateTime generationTime,
      String topic,
      Integer partitions,
      Integer replicas,
      Long durationSeconds,
      @Nullable Map<Integer, Long> latestOffsets,
      @Nullable Map<Integer, Long> minimumLag,
      @Nullable Long aggregateLag,
      @Nullable DateTime offsetsLastUpdated
  )
  {
    super(dataSource, generationTime);
    this.payload = new KafkaSupervisorReportPayload(
        dataSource,
        topic,
        partitions,
        replicas,
        durationSeconds,
        latestOffsets,
        minimumLag,
        aggregateLag,
        offsetsLastUpdated
    );
  }

  @Override
  public Object getPayload()
  {
    return payload;
  }

  public void addTask(TaskReportData data)
  {
    if (data.getType().equals(TaskReportData.TaskType.ACTIVE)) {
      payload.activeTasks.add(data);
    } else if (data.getType().equals(TaskReportData.TaskType.PUBLISHING)) {
      payload.publishingTasks.add(data);
    } else {
      throw new IAE("Unknown task type [%s]", data.getType().name());
    }
  }

  @Override
  public String toString()
  {
    return "{" +
           "id='" + getId() + '\'' +
           ", generationTime=" + getGenerationTime() +
           ", payload=" + payload +
           '}';
  }
}
