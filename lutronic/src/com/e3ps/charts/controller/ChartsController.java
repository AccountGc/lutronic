package com.e3ps.charts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/charts/**")
public class ChartsController extends BaseController {

}
