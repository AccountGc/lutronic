package com.e3ps.change.cr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/cr/**")
public class CrController extends BaseController {

}
