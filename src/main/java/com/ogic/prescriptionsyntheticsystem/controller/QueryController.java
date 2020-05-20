package com.ogic.prescriptionsyntheticsystem.controller;

import com.ogic.prescriptionsyntheticsystem.entity.AprioriRuleView;
import com.ogic.prescriptionsyntheticsystem.entity.BaggingResult;
import com.ogic.prescriptionsyntheticsystem.entity.DrugView;
import com.ogic.prescriptionsyntheticsystem.service.BaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ogic
 */
@Controller
public class QueryController {

    @Autowired
    private BaggingService baggingService;

    @GetMapping("/query")
    public String getQueryView(Model model){
        model.addAttribute("diagnoses", baggingService.getDiagnosisSelectionList());
        return "query";
    }

    @PostMapping("/query")
    public String postQueryView(@RequestParam Integer diagnosis1,
                                @RequestParam Integer diagnosis2,
                                @RequestParam Integer diagnosis3,
                                @RequestParam Integer diagnosis4,
                                Model model){
        List<Integer> diagnoses = new ArrayList<>(4);
        diagnoses.add(diagnosis1);
        diagnoses.add(diagnosis2);
        diagnoses.add(diagnosis3);
        diagnoses.add(diagnosis4);
        List<String> diagnosesStr = new ArrayList<>(4);
        for (int i = 0; i < diagnoses.size(); i++){
            if (diagnoses.get(i) < 10000){
                diagnosesStr.add("00000-无");
            }else {
                diagnosesStr.add(baggingService.getDiagnosisSelectionList().get(diagnoses.get(i) - 10000 + 1).getName());
            }
        }
        model.addAttribute("diagnoses", diagnosesStr);

        for (int i = 0; i < diagnoses.size(); i++){
            if (diagnoses.get(i) < 10000){
                diagnoses.remove(i);
                i--;
            }
        }
        baggingService.aprioriInit();
        BaggingResult result = baggingService.query(diagnoses);

        model.addAttribute("aprioriRuleViews", result.getAprioriRuleViews());
        model.addAttribute("ldaViews", result.getLdaViews());
        model.addAttribute("kmeansViews", result.getKmeansViews());

        List<DrugView> drugViews = new ArrayList<>(3);
        drugViews.add(new DrugView(20049, "二甲双胍片", 48, "片"));
        drugViews.add(new DrugView(20065, "维格列汀(佳维乐)片", 28, "片"));
        drugViews.add(new DrugView(21223, "双环醇片", 36, "片"));

        model.addAttribute("drugViews", drugViews);
        return "query-result";
    }
}
