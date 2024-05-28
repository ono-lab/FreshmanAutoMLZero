package file_templates;

public class TMutationAnalysis {

  static public String begin(int trailNo) {
    return "\\documentclass[twocolumn, a4j]{jarticle}\n\\usepackage{amsmath,amssymb}\n\\usepackage{bm}\n\\usepackage[dvipdfmx]{graphicx}\n\\usepackage[dvipdfmx]{color}\n\\usepackage{ascmac}\n\\usepackage{mathtools}\n\\usepackage{comment}\n\\usepackage{subfigure}\n\\usepackage{algorithm}\n\\usepackage{algorithmic}\n\\usepackage{setspace}\n\\usepackage{multirow}\n\\usepackage{lscape}\n\\usepackage{fullpage}\n\\usepackage{listings,jvlisting}\n\\usepackage{bm}\n\\lstset{\n  basicstyle={\\ttfamily},\n  identifierstyle={\\small},\n  commentstyle={\\smallitshape},\n  keywordstyle={\\small\\boldsymbolseries},\n  ndkeywordstyle={\\small},\n  stringstyle={\\small\\ttfamily},\n  frame={tb},\n  breaklines=true,\n  columns=[l]{fullflexible},\n  numbers=left,\n  xrightmargin=0zw,\n  xleftmargin=3zw,\n  numberstyle={\\scriptsize},\n  stepnumber=1,\n  numbersep=1zw,\n  lineskip=-0.5ex\n}\n\\renewcommand{\\lstlistingname}{コード}\n\\addtolength{\\textheight}{\\topskip}\n\\setlength{\\voffset}{-0.2in}\n\\setlength{\\topmargin}{0pt}\n\\setlength{\\headheight}{0pt}\n\\setlength{\\headsep}{0pt}\n\\setstretch{0.85}\n\\fboxsep=0pt\n\\fboxrule=1pt\n\\title{突然変異解析 No. "
        + trailNo + "}\n\\author{三嶋 隆史}\n\\date{\\today}\n\\begin{document}\n\\maketitle\n\n";

  }

  static public String end() {
    return "\n\\end{document}";
  }
}
