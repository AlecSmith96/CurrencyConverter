package edu.util.currencyconverter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Slf4j
public class Controller
{
  @GetMapping("/")
  public ModelAndView getIndexPage()
  {
      return new ModelAndView("converter");
  }

  @GetMapping("/convert")
  public ModelAndView performConvertion(String first, String second)
  {
      System.out.println(first + ", " + second);
      return new ModelAndView("result");
  }
}
