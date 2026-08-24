import Testing
import Winsplit

@Suite struct WinsplitExportTests {
    @Test func testSwiftModuleLoads() throws {
        let args = split(s: "hello world")
        #expect(args.count == 2)
    }
}
