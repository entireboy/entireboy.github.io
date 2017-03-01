if(args.size() < 1) {
  // TODO usage()
  println("Usage: groovy ${getClass().getName()} name [title] [site]")
  println("  groovy ${getClass().getName()} scala")
  println("  groovy ${getClass().getName()} scala Scala")
  println("  groovy ${getClass().getName()} scala Scala https://www.scala-lang.org/")
  System.exit(0)
}

def name, title, site

// TODO clean
switch(args.size()) {
  case 3:
    name = args[0]
    title = args[1]
    site = args[2]
    break
  case 2:
    name = args[0]
    title = args[1]
    break
  case 1:
    name = args[0]
    title = name
    break
}

def tagFile = new File("./_tags/${name}.md")
if(tagFile.exists()) {
  println("Tag(${name}) is already exists")
  System.exit(0)
}
tagFile.withWriter('UTF-8') {writer ->
  writer.writeLine("---")
  writer.writeLine("name: ${name}")
  writer.writeLine("title: ${title}")
  if(site != null) {
    writer.writeLine("site: ${site}")
  }
  writer.writeLine("---")
}
