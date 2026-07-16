# -*- coding: utf-8 -*-
from pptx import Presentation
from pptx.util import Inches, Pt, Emu
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)

DARK_BLUE = RGBColor(0x1A, 0x3A, 0x5F)
PRIMARY_BLUE = RGBColor(0x16, 0x77, 0xFF)
ACCENT_BLUE = RGBColor(0x18, 0x90, 0xFF)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
DARK_TEXT = RGBColor(0x1D, 0x1D, 0x1F)
GRAY_TEXT = RGBColor(0x6B, 0x72, 0x80)
GREEN = RGBColor(0x52, 0xC4, 0x1A)
ORANGE = RGBColor(0xFA, 0xAD, 0x14)
PURPLE = RGBColor(0x72, 0x2E, 0xD1)
CARD_BG = RGBColor(0xFF, 0xFF, 0xFF)

def add_bg_rect(slide, color=DARK_BLUE):
    shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, prs.slide_width, prs.slide_height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = color
    shape.line.fill.background()

def add_accent_bar(slide, left, top, width, height, color=PRIMARY_BLUE):
    shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = color
    shape.line.fill.background()

def add_text_box(slide, left, top, width, height, text, font_size=14, color=DARK_TEXT, bold=False, alignment=PP_ALIGN.LEFT):
    txBox = slide.shapes.add_textbox(left, top, width, height)
    tf = txBox.text_frame
    tf.word_wrap = True
    p = tf.paragraphs[0]
    p.text = text
    p.font.size = Pt(font_size)
    p.font.color.rgb = color
    p.font.bold = bold
    p.alignment = alignment
    return txBox

def add_card(slide, left, top, width, height, title=None, body=None, title_color=PRIMARY_BLUE):
    shape = slide.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = CARD_BG
    shape.line.color.rgb = RGBColor(0xE5, 0xE7, 0xEB)
    shape.line.width = Pt(1)
    shape.adjustments[0] = 0.05
    if title:
        add_text_box(slide, left + Inches(0.2), top + Inches(0.15), width - Inches(0.4), Inches(0.35), title, font_size=14, color=title_color, bold=True)
    if body:
        add_text_box(slide, left + Inches(0.2), top + Inches(0.5), width - Inches(0.4), height - Inches(0.65), body, font_size=11, color=DARK_TEXT)
    return shape

slide1 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide1)
for l,t,w,h,c in [(Inches(0),Inches(0),Inches(3),Inches(7.5),RGBColor(0x14,0x5A,0xCC)),(Inches(-1),Inches(-1),Inches(4),Inches(4),RGBColor(0x18,0x90,0xFF)),(Inches(6),Inches(5.5),Inches(8),Inches(3),RGBColor(0x14,0x5A,0xCC))]:
    s = slide1.shapes.add_shape(MSO_SHAPE.OVAL if l != Inches(0) else MSO_SHAPE.RECTANGLE, l, t, w, h)
    s.fill.solid()
    s.fill.fore_color.rgb = c
    s.line.fill.background()
add_text_box(slide1, Inches(1.5), Inches(2.6), Inches(10), Inches(1.0), "智汇人才匹配系统", font_size=50, color=WHITE, bold=True)
add_text_box(slide1, Inches(1.5), Inches(3.6), Inches(10), Inches(0.6), "AI-Powered Talent Match Platform", font_size=22, color=RGBColor(0xAE, 0xC6, 0xEB))
add_text_box(slide1, Inches(1.5), Inches(4.5), Inches(9), Inches(0.5), "基于大语言模型的智能简历解析与多维度人岗匹配系统", font_size=16, color=RGBColor(0x8C, 0xAE, 0xE0))
add_text_box(slide1, Inches(1.5), Inches(5.8), Inches(5), Inches(0.4), "项目设计方案  |  2026", font_size=14, color=RGBColor(0x6B, 0x82, 0xAE))
badge_left = Inches(1.5)
for b in ["Spring Boot 2.7", "Vue 3 + ECharts", "DeepSeek API", "MySQL 8.0"]:
    s = slide1.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, badge_left, Inches(6.5), Inches(2.0), Inches(0.35))
    s.fill.solid()
    s.fill.fore_color.rgb = RGBColor(0x1E, 0x4D, 0x8C)
    s.line.fill.background()
    s.adjustments[0] = 0.3
    tf = s.text_frame
    tf.paragraphs[0].text = b
    tf.paragraphs[0].font.size = Pt(10)
    tf.paragraphs[0].font.color.rgb = RGBColor(0xAE, 0xC6, 0xEB)
    tf.paragraphs[0].alignment = PP_ALIGN.CENTER
    badge_left += Inches(2.3)

slide2 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide2, WHITE)
add_accent_bar(slide2, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide2, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "目 录", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide2, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CONTENTS", font_size=14, color=GRAY_TEXT)
toc = [("01","项目背景与目标","省级人才服务平台的数字化转型需求"),("02","系统总体架构","前后端分离 + LLM 微服务架构"),("03","核心技术栈","Spring Boot / Vue 3 / DeepSeek / MySQL"),("04","核心功能详解","用户管理、文件解析、智能匹配、意向沟通"),("05","算法与评分模型","教育 / 城市 / Jaccard / 余弦相似度"),("06","数据库设计","User / Resume / Job / Intent 四张核心表"),("07","前端交互设计","雷达图 / 技能图谱 / 响应式布局"),("08","总结与规划","当前成果与未来演进方向")]
for i,(num,title,desc) in enumerate(toc):
    row, col = i//2, i%2
    left = Inches(0.8)+col*Inches(6.2)
    top = Inches(1.5)+row*Inches(1.25)
    s = slide2.shapes.add_shape(MSO_SHAPE.OVAL, left, top+Inches(0.05), Inches(0.45), Inches(0.45))
    s.fill.solid(); s.fill.fore_color.rgb = PRIMARY_BLUE; s.line.fill.background()
    tf = s.text_frame
    tf.paragraphs[0].text = num
    tf.paragraphs[0].font.size = Pt(14)
    tf.paragraphs[0].font.color.rgb = WHITE
    tf.paragraphs[0].font.bold = True
    tf.paragraphs[0].alignment = PP_ALIGN.CENTER
    tf.vertical_anchor = MSO_ANCHOR.MIDDLE
    add_text_box(slide2, left+Inches(0.6), top, Inches(5.2), Inches(0.35), title, font_size=16, color=DARK_TEXT, bold=True)
    add_text_box(slide2, left+Inches(0.6), top+Inches(0.4), Inches(5.2), Inches(0.3), desc, font_size=11, color=GRAY_TEXT)

slide3 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide3, WHITE)
add_accent_bar(slide3, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide3, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "项目背景与目标", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide3, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "BACKGROUND & OBJECTIVES", font_size=14, color=GRAY_TEXT)
add_card(slide3, Inches(0.6), Inches(1.6), Inches(5.8), Inches(2.5), "项目背景", "传统简历筛选依赖人工\n人才与岗位匹配精度不足\n企业和求职者信息不对称\n急需AI驱动的自动化方案")
add_card(slide3, Inches(6.8), Inches(1.6), Inches(5.8), Inches(2.5), "核心目标", "构建AI简历解析引擎\n实现多维度精准匹配算法\n提供个性化推荐与排序\n建立双向意向沟通机制")
add_card(slide3, Inches(0.6), Inches(4.4), Inches(12), Inches(2.5), "核心创新点", "DeepSeek LLM结构化提取 | 四维加权评分(100分制)\n个人/企业双视角推荐 | ECharts雷达图+力导向图")

slide4 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide4, WHITE)
add_accent_bar(slide4, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide4, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "系统总体架构", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide4, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "SYSTEM ARCHITECTURE", font_size=14, color=GRAY_TEXT)
layers = [("用户层","个人用户/企业用户",PRIMARY_BLUE,Inches(1.4)),("展示层","Vue 3 + ECharts + Element-style UI",ACCENT_BLUE,Inches(2.2)),("API层","RESTful API / Spring Boot Controllers",RGBColor(0x1E,0x4D,0x8C),Inches(3.0)),("业务层","Parse / Match / Intent / Recommend",RGBColor(0x14,0x5A,0xCC),Inches(3.8)),("数据层","MySQL 8.0 + MyBatis-Plus ORM",RGBColor(0x0D,0x3B,0x99),Inches(4.6)),("AI层","DeepSeek LLM + PDFBox + Apache POI",PURPLE,Inches(5.4))]
for n,d,c,t in layers:
    s = slide4.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(2.0), t, Inches(9.3), Inches(0.65))
    s.fill.solid(); s.fill.fore_color.rgb = c; s.line.fill.background(); s.adjustments[0] = 0.15
    tf = s.text_frame; tf.word_wrap = True
    tf.paragraphs[0].text = f"[{n}]"; tf.paragraphs[0].font.size = Pt(14); tf.paragraphs[0].font.color.rgb = WHITE; tf.paragraphs[0].font.bold = True; tf.paragraphs[0].alignment = PP_ALIGN.CENTER
    p2 = tf.add_paragraph(); p2.text = d; p2.font.size = Pt(11); p2.font.color.rgb = RGBColor(0xDD,0xE8,0xFF); p2.alignment = PP_ALIGN.CENTER
for i,(lg,lc) in enumerate([("简历解析",PRIMARY_BLUE),("智能匹配",GREEN),("意向沟通",ORANGE),("可视化",PURPLE)]):
    s = slide4.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(0.6), Inches(1.7)+Inches(1.0)*i, Inches(1.1), Inches(0.35))
    s.fill.solid(); s.fill.fore_color.rgb = lc; s.line.fill.background(); s.adjustments[0]=0.3
    s.text_frame.paragraphs[0].text = lg; s.text_frame.paragraphs[0].font.size = Pt(9); s.text_frame.paragraphs[0].font.color.rgb = WHITE; s.text_frame.paragraphs[0].alignment = PP_ALIGN.CENTER

slide5 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide5, WHITE)
add_accent_bar(slide5, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide5, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心技术栈", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide5, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "TECHNOLOGY STACK", font_size=14, color=GRAY_TEXT)
techs = [("后端框架",["Spring Boot 2.7.18","Java 8","MyBatis-Plus 3.5.3","Maven"],PRIMARY_BLUE),("前端技术",["Vue 3 (Composition API)","Vite构建","ECharts可视化","Axios"],GREEN),("AI与大模型",["DeepSeek Chat","Ollama Qwen2:7b","通义千问DashScope","Prompt工程"],PURPLE),("数据处理",["PDFBox 2.0","Apache POI 5.2","FastJSON","Lombok"],ORANGE),("数据库",["MySQL 8.0","MySQL Connector","HikariCP池","UTF-8 MB4"],RGBColor(0x14,0x5A,0xCC)),("部署",["Spring Boot Jar","Vite产物","Maven","跨平台存储"],GRAY_TEXT)]
for i,(cat,items,color) in enumerate(techs):
    r,c2 = i//3, i%3
    add_card(slide5, Inches(0.6)+c2*Inches(4.2), Inches(1.5)+r*Inches(2.7), Inches(3.9), Inches(2.4), cat, "\n".join(["- "+it for it in items]), color)

slide6 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide6, WHITE)
add_accent_bar(slide6, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide6, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心功能(一): 用户管理与文件解析", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide6, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CORE FEATURES: USER & PARSING", font_size=14, color=GRAY_TEXT)
add_card(slide6, Inches(0.6), Inches(1.5), Inches(5.8), Inches(5.4), "用户管理系统", "注册:角色选择->用户名/密码->手机号/城市\n登录:验证->Session存储userId+role->跳转看板\n权限:个人用户可上传简历/查看推荐/发意向\n企业用户可发布职位/查看候选人/发邀约\n基于Session管理，自动过期")
add_card(slide6, Inches(6.8), Inches(1.5), Inches(5.8), Inches(5.4), "文件上传与智能解析", "上传:.doc/.docx/.pdf (<=10MB) -> UUID重命名 -> uploads/日期目录\n解析: PDFBox/POI提取文本 -> DeepSeek LLM调用 -> JSON清洗 -> 回写数据库\n兜底: LLM失败时使用正则规则提取\n输出: {name,education,skills[],experience,city}\n{title,eduReq,skillReq[],expReq,city}")

slide7 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide7, WHITE)
add_accent_bar(slide7, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide7, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心功能(二): 多维度能力画像", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide7, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CORE FEATURES: PROFILE DIMENSIONS", font_size=14, color=GRAY_TEXT)
dims = [("技术深度","技能数:0->0分,>=5->100分",PRIMARY_BLUE),("项目经验","年数:0->0分,>=6->100分",GREEN),("学历背景","博士100/硕士90/本科70/大专40",ORANGE),("稳定性","基础值70分",PURPLE),("沟通协作","基础值80分",RGBColor(0xEB,0x2F,0x96))]
for i,(n,d,c) in enumerate(dims):
    add_card(slide7, Inches(0.6)+i*Inches(2.5), Inches(1.5), Inches(2.3), Inches(2.0), n, d, c)
add_card(slide7, Inches(0.6), Inches(3.8), Inches(12), Inches(3.2), "能力可视化", "雷达图(ECharts):五边形能力图谱，支持个人vs岗位叠加对比\n力导向图(ForceGraph):技能标签节点自动聚类，展示技能关联网络\n维度评分同时应用于匹配算法，实现分析-可视化-匹配闭环")

slide8 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide8, WHITE)
add_accent_bar(slide8, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide8, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心功能(三): 智能匹配评分模型", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide8, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CORE FEATURES: MATCH SCORING", font_size=14, color=GRAY_TEXT)
add_card(slide8, Inches(0.6), Inches(1.5), Inches(5.8), Inches(2.3), "教育匹配 20分", "求职者学历>=要求->20分\n差一级->10分，差两级以上->0分\n无要求->自动满分")
add_card(slide8, Inches(6.8), Inches(1.5), Inches(5.8), Inches(2.3), "城市匹配 20分", "同城市->20分\n同省份(前2字)->10分\n不同城市->0分\n无要求->自动满分")
add_card(slide8, Inches(0.6), Inches(4.1), Inches(5.8), Inches(2.3), "Jaccard相似度 30分", "J(A,B)=|A交集B|/|A并集B|\n对稀疏技能集敏感\n保守可靠的相似度度量")
add_card(slide8, Inches(6.8), Inches(4.1), Inches(5.8), Inches(2.3), "余弦相似度 30分", "cos(theta)=(A.B)/(||A||*||B||)\n基于词频向量计算\n对频次分布敏感，宽容度高")

slide9 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide9, WHITE)
add_accent_bar(slide9, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide9, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心功能(四): 智能推荐体系", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide9, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CORE FEATURES: RECOMMENDATION", font_size=14, color=GRAY_TEXT)
add_card(slide9, Inches(0.6), Inches(1.5), Inches(5.8), Inches(5.4), "个人用户 - 职位推荐", "流程:上传简历->获取最新简历->遍历所有职位逐对评分->降序Top10\n展示:职位名/公司/匹配度+进度条/学历/城市/技能\n按钮:发送意向(自荐语)\nAPI: GET /api/match/recommend/jobs")
add_card(slide9, Inches(6.8), Inches(1.5), Inches(5.8), Inches(5.4), "企业用户 - 候选人推荐", "流程:上传职位->获取最新职位->遍历所有简历逐对评分->降序Top10\n展示:候选人名(脱敏)/匹配度/学历/年限/城市/技能\n按钮:发起邀约(邀约语)\nAPI: GET /api/match/recommend/candidates")

slide10 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide10, WHITE)
add_accent_bar(slide10, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide10, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "核心功能(五): 意向沟通机制", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide10, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "CORE FEATURES: INTENT MANAGEMENT", font_size=14, color=GRAY_TEXT)
add_card(slide10, Inches(0.6), Inches(1.5), Inches(5.8), Inches(5.4), "意向沟通流程", "个人->企业: 发送意向->填写自荐语->待回应->企业接受->联系方式解锁\n企业->个人: 发起邀约->填写邀约语->待回应->个人接受->联系方式解锁\n状态: 待回应(0)->已接受(1)/已拒绝(2)/已过期(3)\n默认7天过期，双方接受后解锁手机号+邮箱")
add_card(slide10, Inches(6.8), Inches(1.5), Inches(5.8), Inches(5.4), "意向管理页面", "双Tab: 我发出的 / 我收到的\n操作: 待回应->[接受][拒绝]\n已接受->[查看联系方式]\n已拒绝/已过期->仅展示状态\n防重复: 相同组合只能有一条待回应记录")

slide11 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide11, WHITE)
add_accent_bar(slide11, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide11, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "数据库设计", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide11, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "DATABASE SCHEMA", font_size=14, color=GRAY_TEXT)
tables = [("user用户表","id PK, username UNIQUE, password, role(0个人/1企业), phone, city, create_time"),("resume简历表","id PK, user_id FK, file_path, parsed_json(JSON), create_time"),("job职位表","id PK, user_id FK, file_path, parsed_json(JSON), create_time"),("intent意向表","id PK, from_user_id, to_user_id, resume_id/job_id, type, status(0/1/2/3), message, expire_time, create_time, update_time")]
for i,(n,s) in enumerate(tables):
    c,r = i%2, i//2
    add_card(slide11, Inches(0.6)+c*Inches(6.2), Inches(1.5)+r*Inches(2.8), Inches(5.9), Inches(2.5), n, s)

slide12 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide12, WHITE)
add_accent_bar(slide12, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide12, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "前端交互与可视化设计", font_size=26, bold=True, color=DARK_TEXT)
add_text_box(slide12, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "UI/UX & VISUALIZATION", font_size=14, color=GRAY_TEXT)
ui = [("响应式布局","侧边栏+主内容,Grid自适应4->2->1列,移动端隐藏侧边栏",PRIMARY_BLUE),("ECharts雷达图","五边形能力图谱,蓝(个人)/黄(岗位)双色对比",GREEN),("力导向图","技能标签节点自动聚类,节点大小=重要性",PURPLE),("轮播Banner","首页/登录/工作台均支持,4s自动+手动导航",ORANGE)]
for i,(n,d,c) in enumerate(ui):
    add_card(slide12, Inches(0.6)+i*Inches(3.2), Inches(1.5), Inches(3.0), Inches(2.5), n, d, c)
add_card(slide12, Inches(0.6), Inches(4.3), Inches(12), Inches(2.7), "前端核心页面 (11个Vue页面)", "HomePage首页 / login+register登录注册 / Dashboard工作台\nPersonal个人中心+Enterprise企业中心 / Recommend职位推荐\nCandidates候选人推荐 / IntentManagement意向管理\nAppSidebar侧边栏 / RadarChart雷达图 / ForceGraph力导向图")

slide13 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide13, WHITE)
add_accent_bar(slide13, Inches(0.6), Inches(0.5), Inches(0.08), Inches(0.4), PRIMARY_BLUE)
add_text_box(slide13, Inches(0.85), Inches(0.45), Inches(10), Inches(0.5), "关键数据流程", font_size=28, bold=True, color=DARK_TEXT)
add_text_box(slide13, Inches(0.85), Inches(0.95), Inches(10), Inches(0.4), "KEY DATA FLOWS", font_size=14, color=GRAY_TEXT)
flows = [("简历解析","上传->校验->UUID存储->PDFBox/POI提取->DeepSeek LLM->JSON清洗->回写"),("匹配计算","获取简历+职位JSON->解析字段->计算教育+城市+Jaccard+余弦->总分->MatchResult"),("推荐排序","获取最新简历/职位->遍历全部逐对计算->总分降序->Top10->返回前端"),("意向沟通","发送->防重复检查->创建待回应记录->目标用户查看->接受(解锁联系方式)/拒绝")]
for i,(t,d) in enumerate(flows):
    top = Inches(1.5)+i*Inches(1.35)
    s = slide13.shapes.add_shape(MSO_SHAPE.OVAL, Inches(0.6), top+Inches(0.05), Inches(0.35), Inches(0.35))
    s.fill.solid(); s.fill.fore_color.rgb = PRIMARY_BLUE; s.line.fill.background()
    s.text_frame.paragraphs[0].text = str(i+1); s.text_frame.paragraphs[0].font.size = Pt(12); s.text_frame.paragraphs[0].font.color.rgb = WHITE; s.text_frame.paragraphs[0].font.bold = True; s.text_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    add_text_box(slide13, Inches(1.1), top, Inches(2.0), Inches(0.35), t, font_size=13, color=DARK_BLUE, bold=True)
    add_text_box(slide13, Inches(3.2), top+Inches(0.02), Inches(9.5), Inches(1.2), d, font_size=10.5, color=GRAY_TEXT)

slide14 = prs.slides.add_slide(prs.slide_layouts[6])
add_bg_rect(slide14, DARK_BLUE)
add_text_box(slide14, Inches(0.8), Inches(0.6), Inches(11), Inches(0.6), "总结与未来规划", font_size=32, color=WHITE, bold=True)
add_text_box(slide14, Inches(0.8), Inches(1.1), Inches(11), Inches(0.4), "SUMMARY & FUTURE ROADMAP", font_size=16, color=RGBColor(0xAE,0xC6,0xEB))
add_card(slide14, Inches(0.8), Inches(1.8), Inches(5.6), Inches(4.0), "已实现成果(GREEN)", "1.完整用户注册/登录/角色管理体系\n2.多格式文件上传与AI智能解析引擎\n3.五维度能力画像与ECharts可视化\n4.四维加权匹配评分算法(100分制)\n5.个人/企业双视角Top10推荐系统\n6.双向意向沟通与联系方式解锁\n7.Session用户权限控制\n8.响应式布局+雷达图/力导向图",GREEN)
add_card(slide14, Inches(6.8), Inches(1.8), Inches(5.6), Inches(4.0), "未来演进方向(ORANGE)", "1.OCR图片文字提取\n2.RAG检索增强生成\n3.权重学习+用户反馈闭环\n4.历史版本管理与趋势分析\n5.在线简历编辑器\n6.邮件/短信消息推送\n7.管理者看板与统计报表\n8.微服务+Docker/K8s容器化",ORANGE)
add_text_box(slide14, Inches(0.8), Inches(6.3), Inches(11), Inches(0.4), "智汇人才匹配系统 - 以AI技术解决人才招聘中的信息不对称问题", font_size=13, color=RGBColor(0xAE,0xC6,0xEB))
add_text_box(slide14, Inches(0.8), Inches(6.8), Inches(5), Inches(0.4), "(c) 2026 智汇人才匹配系统 | 项目设计方案", font_size=10, color=RGBColor(0x6B,0x82,0xAE))

output_path = "D:\\job\\智汇人才匹配系统_设计方案.pptx"
prs.save(output_path)
print(f"PPT saved: {output_path}")
print(f"Total slides: {len(prs.slides)}")
