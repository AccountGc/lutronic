package com.e3ps.change.ecn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/ecn/**")
public class EcnController extends BaseController {

}
