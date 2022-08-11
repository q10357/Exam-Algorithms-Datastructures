public class Ex01 {

    public String regexPartA(String module){
        return "(\\/pg4200algorithms)*\\/(lessons|solutions|exercises)\\" +
                "/src\\/(test|main)\\/java\\/org\\/pg4200\\/(les|sol|ex)" + module + "\\/.*\\.(java|kt|cpp)";
    }

    public String regexPartB(){
        return "@.+: .*(((PG|pg)([A-Z]{0,1})[0-9]{3,4})|(programmering|programming)).*(([0-9]{4}\\-[0-9]{2}\\-[0-9]{2})|([0-9]{2}\\-[0-9]{2}\\-[0-9]{2})).*";
    }
}
