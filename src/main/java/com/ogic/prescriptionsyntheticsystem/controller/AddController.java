package com.ogic.prescriptionsyntheticsystem.controller;

import com.ogic.prescriptionsyntheticsystem.entity.*;
import com.ogic.prescriptionsyntheticsystem.service.BaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ogic
 */
@Controller
public class AddController {

    @Autowired
    private BaggingService baggingService;

    @GetMapping("/add")
    public String getAddView(Model model) throws IOException {
        model.addAttribute("diagnoses", baggingService.getDiagnosisSelectionList());
        model.addAttribute("drugs", baggingService.getDrugSelectionList());
        return "add";
    }

    @PostMapping("/add")
    public String addPrescription(PrescriptionView prescriptionView,
                                  Model model){
        Sample sample = prescriptionView.toSample();
        baggingService.aprioriInit();
        BaggingResult result = baggingService.judge(sample);

        List<Integer> diagnoses = sample.getDiagnoses();
        List<String> diagnosesStr = new ArrayList<>(4);
        for (int i = 0; i < 4; i++){
            if (diagnoses.size() <= i || diagnoses.get(i) < 10000){
                diagnosesStr.add("00000-无");
            }else {
                diagnosesStr.add(baggingService.getDiagnosisSelectionList().get(diagnoses.get(i) - 10000 + 1).getName());
            }
        }
        model.addAttribute("diagnoses", diagnosesStr);

        model.addAttribute("drugViews", result.getDrugViews());
        model.addAttribute("aprioriRuleViews", result.getAprioriRuleViews());
        model.addAttribute("ldaViews", result.getLdaViews());
        model.addAttribute("kmeansViews", result.getKmeansViews());
        return "add-judge";
    }
}
