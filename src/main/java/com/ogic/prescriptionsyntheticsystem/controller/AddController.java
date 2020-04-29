package com.ogic.prescriptionsyntheticsystem.controller;

import com.ogic.prescriptionsyntheticsystem.entity.PrescriptionView;
import com.ogic.prescriptionsyntheticsystem.service.BaggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ogic
 */
@Controller
public class AddController {

    @Autowired
    private BaggingService baggingService;

    @GetMapping("/add")
    public String getAddView(Model model){
        model.addAttribute("diagnoses", baggingService.getDiagnosisSelectionList());
        model.addAttribute("drugs", baggingService.getDrugSelectionList());
        return "add";
    }

    @PostMapping("/add")
    public String addPrescription(PrescriptionView prescriptionView,
                                  Model model){
        System.out.println(prescriptionView);
        return "add_check";
    }
}
