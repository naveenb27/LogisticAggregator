package com.example.LogisticAggregator.DTO;

import com.example.LogisticAggregator.Model.HubManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubManagerAssigner {
    private Long hubId;
    private HubManager hubManager;
}
