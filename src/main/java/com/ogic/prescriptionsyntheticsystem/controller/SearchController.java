package com.ogic.prescriptionsyntheticsystem.controller;

import com.ogic.prescriptionsyntheticsystem.entity.Selection;
import com.ogic.prescriptionsyntheticsystem.service.BaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    BaggingService baggingService;

    @GetMapping("/search/{name}")
    public String search(@PathVariable("name") String name){
        List<Selection> diagnosisSelectionList = baggingService.getDiagnosisSelectionList();
        List<Selection> drugSelectionList = baggingService.getDrugSelectionList();
        List<Selection> result = new ArrayList<>();
        for (Selection selection: diagnosisSelectionList){
            if (selection.getName().contains(name)){
                result.add(selection);
            }
        }
        for (Selection selection: drugSelectionList){
            if (selection.getName().contains(name)){
                result.add(selection);
            }
        }
        return Arrays.toString(result.toArray());
    }
}
